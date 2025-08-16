-- ===============================================
-- SPARK 데모 사용자 계정 생성 스크립트
-- ===============================================
-- 실행 방법: psql -h localhost -p 5432 -U root -d spark -f demo_users.sql
-- 
-- 이 파일은 데모/테스트용 사용자 계정만 생성합니다.
-- 미션: quick_missions.sql
-- 리워드: init_data.sql

-- 기존 데모 사용자 정보 업데이트 (Long ID 사용)
INSERT INTO users (
    name, email, password, avatar_url, level, level_title, current_points, total_points,
    current_streak, longest_streak, completed_missions, total_days, preferences,
    this_month_points, this_month_missions, average_rating, total_ratings,
    category_stats, created_at, updated_at, last_login_at
) VALUES 
(
    '테스트유저1',
    'testuser1@spark.com',
    '$2a$10$demopassword.encrypted.hash.for.testing.purposes.only',
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
    5,
    'EXPLORER', 
    450,
    1200,
    5,
    12,
    18,
    25,
    '{"notifications": true, "email_alerts": true}',
    450,
    8,
    4.5,
    10,
    '{"ADVENTURE": 5, "HEALTH": 4, "CREATIVE": 3, "LEARNING": 3, "SOCIAL": 3}',
    NOW(),
    NOW(),
    NOW()
),
(
    '테스트유저2',
    'testuser2@spark.com',
    '$2a$10$demopassword.encrypted.hash.for.testing.purposes.only',
    'https://images.unsplash.com/photo-1494790108755-2616b9e6e3e7?w=150&h=150&fit=crop&crop=face',
    8,
    'EXPLORER',
    720,
    2500,
    12,
    25,
    35,
    42,
    '{"notifications": true, "email_alerts": false}',
    720,
    15,
    4.2,
    25,
    '{"ADVENTURE": 8, "HEALTH": 7, "CREATIVE": 6, "LEARNING": 8, "SOCIAL": 6}',
    NOW(),
    NOW(),
    NOW()
),
(
    '고급유저',
    'premium@spark.com',
    '$2a$10$demopassword.encrypted.hash.for.testing.purposes.only',
    'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    15,
    'ADVENTURER',
    1800,
    8500,
    25,
    45,
    85,
    120,
    '{"notifications": true, "email_alerts": true}',
    1800,
    35,
    4.8,
    60,
    '{"ADVENTURE": 20, "HEALTH": 18, "CREATIVE": 15, "LEARNING": 17, "SOCIAL": 15}',
    NOW(),
    NOW(),
    NOW()
)
ON CONFLICT (email) DO UPDATE SET
    name = EXCLUDED.name,
    level = EXCLUDED.level,
    level_title = EXCLUDED.level_title,
    current_points = EXCLUDED.current_points,
    total_points = EXCLUDED.total_points,
    current_streak = EXCLUDED.current_streak,
    longest_streak = EXCLUDED.longest_streak,
    completed_missions = EXCLUDED.completed_missions,
    total_days = EXCLUDED.total_days,
    preferences = EXCLUDED.preferences,
    this_month_points = EXCLUDED.this_month_points,
    this_month_missions = EXCLUDED.this_month_missions,
    average_rating = EXCLUDED.average_rating,
    total_ratings = EXCLUDED.total_ratings,
    category_stats = EXCLUDED.category_stats,
    updated_at = NOW(),
    last_login_at = NOW();

-- ===============================================
-- 데모 사용자용 user_stats 생성
-- ===============================================

-- 먼저 데모 사용자들의 실제 ID를 가져와서 user_stats 생성
INSERT INTO user_stats (
    user_id, 
    adventurous_allocated, adventurous_current,
    available_points,
    created_at,
    creativity_allocated, creativity_current,
    discipline_allocated, discipline_current,
    intelligence_allocated, intelligence_current,
    last_updated_at,
    sociability_allocated, sociability_current,
    strength_allocated, strength_current,
    total_earned_points
)
SELECT 
    u.id,
    -- adventurous_allocated, adventurous_current
    CASE u.level
        WHEN 5 THEN 10
        WHEN 8 THEN 18
        WHEN 15 THEN 30
    END,
    CASE u.level
        WHEN 5 THEN 14
        WHEN 8 THEN 25
        WHEN 15 THEN 40
    END,
    -- available_points
    CASE u.level
        WHEN 5 THEN 3
        WHEN 8 THEN 8
        WHEN 15 THEN 15
    END,
    -- created_at
    NOW(),
    -- creativity_allocated, creativity_current
    CASE u.level
        WHEN 5 THEN 6
        WHEN 8 THEN 10
        WHEN 15 THEN 18
    END,
    CASE u.level
        WHEN 5 THEN 8
        WHEN 8 THEN 15
        WHEN 15 THEN 28
    END,
    -- discipline_allocated, discipline_current
    CASE u.level
        WHEN 5 THEN 12
        WHEN 8 THEN 15
        WHEN 15 THEN 22
    END,
    CASE u.level
        WHEN 5 THEN 12
        WHEN 8 THEN 16
        WHEN 15 THEN 24
    END,
    -- intelligence_allocated, intelligence_current
    CASE u.level
        WHEN 5 THEN 8
        WHEN 8 THEN 12
        WHEN 15 THEN 20
    END,
    CASE u.level
        WHEN 5 THEN 10
        WHEN 8 THEN 18
        WHEN 15 THEN 32
    END,
    -- last_updated_at
    NOW(),
    -- sociability_allocated, sociability_current
    CASE u.level
        WHEN 5 THEN 12
        WHEN 8 THEN 16
        WHEN 15 THEN 24
    END,
    CASE u.level
        WHEN 5 THEN 15
        WHEN 8 THEN 22
        WHEN 15 THEN 38
    END,
    -- strength_allocated, strength_current
    CASE u.level
        WHEN 5 THEN 10
        WHEN 8 THEN 15
        WHEN 15 THEN 25
    END,
    CASE u.level
        WHEN 5 THEN 12
        WHEN 8 THEN 20
        WHEN 15 THEN 35
    END,
    -- total_earned_points
    u.total_points
FROM users u 
WHERE u.email IN ('testuser1@spark.com', 'testuser2@spark.com', 'premium@spark.com')
ON CONFLICT (user_id) DO UPDATE SET
    adventurous_allocated = EXCLUDED.adventurous_allocated,
    adventurous_current = EXCLUDED.adventurous_current,
    available_points = EXCLUDED.available_points,
    creativity_allocated = EXCLUDED.creativity_allocated,
    creativity_current = EXCLUDED.creativity_current,
    discipline_allocated = EXCLUDED.discipline_allocated,
    discipline_current = EXCLUDED.discipline_current,
    intelligence_allocated = EXCLUDED.intelligence_allocated,
    intelligence_current = EXCLUDED.intelligence_current,
    sociability_allocated = EXCLUDED.sociability_allocated,
    sociability_current = EXCLUDED.sociability_current,
    strength_allocated = EXCLUDED.strength_allocated,
    strength_current = EXCLUDED.strength_current,
    total_earned_points = EXCLUDED.total_earned_points,
    last_updated_at = NOW();

-- ===============================================
-- 데이터 확인 쿼리 (참고용)
-- ===============================================

-- SELECT '=== 데모 사용자 수 ===' as info, COUNT(*) as count FROM users WHERE email LIKE '%spark.com';

-- ===============================================
-- 데모 사용자 생성 완료!
-- ===============================================
-- 이 스크립트로 다음이 생성됩니다:
-- - 3개의 데모 사용자 계정 (다양한 레벨과 포인트)
--   * 테스트유저1: LV.5 EXPLORER (450/1200 포인트)
--   * 테스트유저2: LV.8 EXPLORER (720/2500 포인트)  
--   * 고급유저: LV.15 ADVENTURER (1800/8500 포인트)
-- 
-- 중복 실행 안전: ON CONFLICT 구문으로 안전한 재실행 가능
-- 
-- 완전한 서비스 이용을 위해서는:
-- 1. quick_missions.sql (미션 50개)
-- 2. init_data.sql (리워드 19개)
-- 3. demo_users.sql (사용자 3명) ← 현재 파일
-- 순서대로 실행하시기 바랍니다.