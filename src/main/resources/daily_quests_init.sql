-- ===============================================
-- SPARK 일일 퀘스트 초기 데이터 설정 스크립트
-- ===============================================
-- "삶을 게임처럼 즐겨라!" - 매일의 루틴을 게임화하는 퀘스트들
--
-- 실행 방법: 
-- psql -h localhost -p 5432 -U root -d spark -f daily_quests_init.sql
--
-- 또는 애플리케이션 시작 시 자동 실행을 위해 
-- application.yml의 spring.sql.init.data-locations에 추가
-- ===============================================

-- 기존 일일 퀘스트 데이터 정리 (개발용 - 프로덕션에서는 주의!)
-- DELETE FROM daily_quest_progress;
-- DELETE FROM daily_quest_summary;
-- DELETE FROM daily_quests;

-- ===============================================
-- 1. 기본 일일 퀘스트 4개 등록
-- ===============================================

INSERT INTO daily_quests (
    quest_type, 
    title, 
    description, 
    icon, 
    quest_order, 
    reward_points, 
    stat_reward, 
    is_active,
    created_at,
    updated_at
) VALUES 
-- 1. 이불 개기 (아침 루틴의 시작)
(
    'MAKE_BED',
    '이불 개기',
    '일어나서 이불을 정리하세요',
    '🛏️',
    1,
    5,
    'DISCIPLINE',
    true,
    NOW(),
    NOW()
),

-- 2. 샤워하기 (개인 위생 관리)
(
    'TAKE_SHOWER',
    '샤워하기',
    '깔끔하게 샤워를 하세요',
    '🚿',
    2,
    5,
    'DISCIPLINE',
    true,
    NOW(),
    NOW()
),

-- 3. 집 청소하기 (생활 공간 정리)
(
    'CLEAN_HOUSE',
    '집 청소하기',
    '주변을 깨끗하게 정리하세요',
    '🧹',
    3,
    5,
    'DISCIPLINE',
    true,
    NOW(),
    NOW()
),

-- 4. 감사 일기 (마음의 정리)
(
    'GRATITUDE_JOURNAL',
    '감사 일기',
    '감사한 일 한 가지를 생각해보세요',
    '🙏',
    4,
    5,
    'DISCIPLINE',
    true,
    NOW(),
    NOW()
);

-- ===============================================
-- 2. 확인 쿼리 (데이터 삽입 확인용)
-- ===============================================

-- 삽입된 일일 퀘스트 확인
SELECT 
    id,
    quest_type,
    title,
    description,
    icon,
    quest_order,
    reward_points,
    is_active,
    created_at
FROM daily_quests 
ORDER BY quest_order;

-- ===============================================
-- 3. 인덱스 생성 (성능 최적화)
-- ===============================================

-- 일일 퀘스트 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_daily_quest_type ON daily_quests(quest_type);
CREATE INDEX IF NOT EXISTS idx_daily_quest_active ON daily_quests(is_active);
CREATE INDEX IF NOT EXISTS idx_daily_quest_order ON daily_quests(quest_order);

-- 일일 퀘스트 진행 상황 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_daily_progress_user_date ON daily_quest_progress(user_id, quest_date);
CREATE INDEX IF NOT EXISTS idx_daily_progress_user_completed ON daily_quest_progress(user_id, is_completed);
CREATE INDEX IF NOT EXISTS idx_daily_progress_date ON daily_quest_progress(quest_date);
CREATE INDEX IF NOT EXISTS idx_daily_progress_quest_type ON daily_quest_progress(quest_type);

-- 일일 퀘스트 요약 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_daily_summary_user_date ON daily_quest_summary(user_id, summary_date);
CREATE INDEX IF NOT EXISTS idx_daily_summary_date ON daily_quest_summary(summary_date);
CREATE INDEX IF NOT EXISTS idx_daily_summary_completion ON daily_quest_summary(completion_percentage);
CREATE INDEX IF NOT EXISTS idx_daily_summary_user_completion ON daily_quest_summary(user_id, completion_percentage);

-- ===============================================
-- 4. 통계 정보 (선택사항)
-- ===============================================

-- 테이블 생성 확인
SELECT 
    schemaname,
    tablename,
    tableowner
FROM pg_tables 
WHERE tablename LIKE 'daily_quest%'
ORDER BY tablename;

-- 컬럼 정보 확인
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'daily_quests'
ORDER BY ordinal_position;

COMMIT;