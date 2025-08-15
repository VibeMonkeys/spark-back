-- 업적 시스템 샘플 데이터
-- 사용자 user_01에게 몇 개의 업적을 발급하여 테스트용 데이터를 만듦

-- user_01의 첫 번째 미션 완료 업적 발급
INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('user_01', 'FIRST_MISSION', NOW(), 100, false, NOW(), NOW());

-- user_01의 3일 연속 업적 발급  
INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('user_01', 'MISSION_STREAK_3', NOW(), 100, false, NOW(), NOW());

-- user_01의 10개 미션 완료 업적 발급
INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('user_01', 'MISSIONS_10', NOW(), 100, false, NOW(), NOW());

-- user_01의 1000 포인트 업적 발급
INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('user_01', 'POINTS_1000', NOW(), 100, false, NOW(), NOW());

-- user_01의 진행 중인 업적들 (50% 진행도)
INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('user_01', 'MISSION_STREAK_7', NOW(), 50, false, NOW(), NOW());

INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('user_01', 'MISSIONS_50', NOW(), 30, false, NOW(), NOW());

-- 실제 사용자에게도 몇 개 발급 (c1f9142f-e2d4-4406-860a-03b2679152d2)
INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('c1f9142f-e2d4-4406-860a-03b2679152d2', 'FIRST_MISSION', NOW(), 100, false, NOW(), NOW());

INSERT INTO user_achievements (user_id, achievement_type, unlocked_at, progress, is_notified, created_at, updated_at)
VALUES ('c1f9142f-e2d4-4406-860a-03b2679152d2', 'MISSION_STREAK_3', NOW(), 100, false, NOW(), NOW());