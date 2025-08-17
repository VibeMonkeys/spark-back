-- PostgreSQL GIN 인덱스 및 해시태그 검색 성능 최적화
-- 이 스크립트는 해시태그 검색 기능의 성능을 최적화하기 위한 인덱스들을 생성합니다

-- 1. 스토리 테이블의 해시태그 검색을 위한 GIN 인덱스
-- 전체 텍스트 검색을 위한 GIN 인덱스 (user_tags + auto_tags)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_hashtags_gin 
ON stories USING GIN (
    to_tsvector('simple', COALESCE(user_tags, '') || ' ' || COALESCE(auto_tags, ''))
);

-- 개별 해시태그 배열 검색을 위한 GIN 인덱스
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_user_tags_gin 
ON stories USING GIN (user_tags);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_auto_tags_gin 
ON stories USING GIN (auto_tags);

-- 2. 해시태그 통계 테이블의 성능 최적화 인덱스들
-- 해시태그별 검색 최적화 (이미 entity에 정의되어 있지만 명시적으로 추가)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_hashtag_date 
ON hashtag_stats (hashtag, date);

-- 트렌딩 해시태그 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_trending 
ON hashtag_stats (date, trend_score DESC, daily_count DESC) 
WHERE trend_score >= 20.0 AND daily_count >= 5;

-- 인기 해시태그 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_popular 
ON hashtag_stats (date, daily_count DESC) 
WHERE daily_count >= 10 OR weekly_count >= 50 OR trend_score >= 10.0;

-- 자동완성을 위한 prefix 검색 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_prefix 
ON hashtag_stats USING GIN (hashtag gin_trgm_ops);

-- 3. 스토리 테이블의 해시태그 관련 추가 인덱스들
-- 해시태그 포함 스토리의 최신순 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_hashtag_created_at 
ON stories (created_at DESC) 
WHERE user_tags IS NOT NULL OR auto_tags IS NOT NULL;

-- 공개 스토리 중 해시태그 포함 스토리 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_public_hashtags 
ON stories (is_public, created_at DESC) 
WHERE (user_tags IS NOT NULL OR auto_tags IS NOT NULL) AND is_public = true;

-- 4. 전체 텍스트 검색을 위한 추가 최적화
-- 스토리 내용과 해시태그를 함께 검색하기 위한 복합 인덱스
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_fulltext_search 
ON stories USING GIN (
    to_tsvector('simple', 
        story_text || ' ' || 
        COALESCE(user_tags, '') || ' ' || 
        COALESCE(auto_tags, '') || ' ' ||
        COALESCE(location, '')
    )
);

-- 5. 통계 및 집계 쿼리 최적화
-- 날짜별 해시태그 사용량 집계를 위한 인덱스
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_aggregation 
ON hashtag_stats (date, hashtag, total_count);

-- 주간/월간 통계 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_weekly 
ON hashtag_stats (date, weekly_count DESC) 
WHERE weekly_count > 0;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_monthly 
ON hashtag_stats (date, monthly_count DESC) 
WHERE monthly_count > 0;

-- 6. pg_trgm 확장 활성화 (자동완성 기능을 위해)
-- 이 확장은 슈퍼유저 권한이 필요할 수 있습니다
-- CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 7. 성능 모니터링을 위한 통계 정보 수집
-- PostgreSQL 통계 수집기 활성화
ANALYZE stories;
ANALYZE hashtag_stats;

-- 8. 인덱스 사용량 모니터링 뷰 (개발/운영 환경에서 성능 모니터링용)
-- SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
-- FROM pg_stat_user_indexes 
-- WHERE tablename IN ('stories', 'hashtag_stats')
-- ORDER BY idx_scan DESC;

-- 9. 해시태그 검색 성능 테스트 쿼리 예시
/*
-- 특정 해시태그 검색 테스트
EXPLAIN ANALYZE 
SELECT * FROM stories 
WHERE user_tags @> '["#카페"]' OR auto_tags @> '["#카페"]'
ORDER BY created_at DESC 
LIMIT 20;

-- 전체 텍스트 검색 테스트
EXPLAIN ANALYZE
SELECT * FROM stories 
WHERE to_tsvector('simple', story_text || ' ' || COALESCE(user_tags, '') || ' ' || COALESCE(auto_tags, '')) 
@@ to_tsquery('simple', '카페 | 커피')
ORDER BY created_at DESC 
LIMIT 20;

-- 트렌딩 해시태그 조회 테스트
EXPLAIN ANALYZE
SELECT * FROM hashtag_stats 
WHERE date = CURRENT_DATE 
AND trend_score >= 20.0 
AND daily_count >= 5 
ORDER BY trend_score DESC 
LIMIT 10;
*/

-- 10. 인덱스 생성 완료 확인
SELECT 
    schemaname,
    tablename, 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename IN ('stories', 'hashtag_stats')
AND indexname LIKE '%hashtag%' OR indexname LIKE '%gin%'
ORDER BY tablename, indexname;