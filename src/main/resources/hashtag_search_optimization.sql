-- PostgreSQL 해시태그 검색 성능 최적화 인덱스
-- 수정된 버전 - 텍스트 필드 GIN 인덱스 문제 해결

-- 1. PostgreSQL 확장 활성화 (pg_trgm은 trigram 검색을 위해 필요)
-- 참고: 이 확장은 데이터베이스 관리자 권한이 필요할 수 있습니다
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 2. 스토리 테이블의 해시태그 텍스트 검색을 위한 GIN 인덱스
-- user_tags와 auto_tags는 TEXT 타입이므로 gin_trgm_ops를 사용
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_user_tags_gin_trgm 
ON stories USING GIN (user_tags gin_trgm_ops);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_auto_tags_gin_trgm 
ON stories USING GIN (auto_tags gin_trgm_ops);

-- 3. 전체 텍스트 검색을 위한 복합 인덱스 (tsvector 사용)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_fulltext_search 
ON stories USING GIN (
    to_tsvector('simple', 
        COALESCE(story_text, '') || ' ' || 
        COALESCE(user_tags, '') || ' ' || 
        COALESCE(auto_tags, '') || ' ' ||
        COALESCE(location, '')
    )
);

-- 4. 해시태그 통계 테이블의 추가 최적화 인덱스들
-- trigram 검색을 위한 GIN 인덱스 (자동완성용)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_hashtag_gin_trgm 
ON hashtag_stats USING GIN (hashtag gin_trgm_ops);

-- 트렌딩 해시태그 조회 최적화 (조건부 인덱스)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_trending 
ON hashtag_stats (date, trend_score DESC, daily_count DESC) 
WHERE trend_score >= 20.0 AND daily_count >= 5;

-- 인기 해시태그 조회 최적화 (조건부 인덱스)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_popular 
ON hashtag_stats (date, daily_count DESC) 
WHERE daily_count >= 10 OR weekly_count >= 50 OR trend_score >= 10.0;

-- 5. 스토리 테이블의 해시태그 관련 추가 인덱스들
-- 해시태그 포함 스토리의 최신순 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_hashtag_created_at 
ON stories (created_at DESC) 
WHERE user_tags IS NOT NULL OR auto_tags IS NOT NULL;

-- 공개 스토리 중 해시태그 포함 스토리 조회 최적화
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_stories_public_hashtags 
ON stories (is_public, created_at DESC) 
WHERE (user_tags IS NOT NULL OR auto_tags IS NOT NULL) AND is_public = true;

-- 6. 통계 및 집계 쿼리 최적화
-- 날짜별 해시태그 사용량 집계를 위한 인덱스
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_aggregation 
ON hashtag_stats (date, hashtag, total_count);

-- 주간/월간 통계 조회 최적화 (조건부 인덱스)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_weekly 
ON hashtag_stats (date, weekly_count DESC) 
WHERE weekly_count > 0;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_hashtag_stats_monthly 
ON hashtag_stats (date, monthly_count DESC) 
WHERE monthly_count > 0;

-- 7. 성능 모니터링을 위한 통계 정보 수집
ANALYZE stories;
ANALYZE hashtag_stats;

-- 8. 생성된 인덱스 확인
SELECT 
    schemaname,
    tablename, 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename IN ('stories', 'hashtag_stats')
AND (indexname LIKE '%hashtag%' OR indexname LIKE '%gin%' OR indexname LIKE '%trgm%')
ORDER BY tablename, indexname;

-- 9. 성능 테스트 쿼리 예시 (주석 처리)
/*
-- 해시태그 포함 검색 테스트
EXPLAIN ANALYZE 
SELECT * FROM stories 
WHERE user_tags ILIKE '%#카페%' OR auto_tags ILIKE '%#카페%'
ORDER BY created_at DESC 
LIMIT 20;

-- 자동완성 검색 테스트
EXPLAIN ANALYZE
SELECT hashtag FROM hashtag_stats 
WHERE hashtag % '#카' 
AND date = CURRENT_DATE
ORDER BY similarity(hashtag, '#카') DESC, total_count DESC 
LIMIT 10;

-- 트렌딩 해시태그 조회 테스트
EXPLAIN ANALYZE
SELECT * FROM hashtag_stats 
WHERE date = CURRENT_DATE 
AND trend_score >= 20.0 
AND daily_count >= 5 
ORDER BY trend_score DESC 
LIMIT 10;

-- 전체 텍스트 검색 테스트
EXPLAIN ANALYZE
SELECT * FROM stories 
WHERE to_tsvector('simple', 
    COALESCE(story_text, '') || ' ' || 
    COALESCE(user_tags, '') || ' ' || 
    COALESCE(auto_tags, '') || ' ' ||
    COALESCE(location, '')) 
@@ to_tsquery('simple', '카페 | 커피')
ORDER BY created_at DESC 
LIMIT 20;
*/