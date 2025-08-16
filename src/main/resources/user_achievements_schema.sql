-- 사용자 업적 테이블 생성
CREATE TABLE IF NOT EXISTS user_achievements (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    achievement_type VARCHAR(50) NOT NULL,
    unlocked_at TIMESTAMP NOT NULL,
    progress INTEGER NOT NULL DEFAULT 0,
    is_notified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_user_achievements_user_id ON user_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_user_achievements_achievement_type ON user_achievements(achievement_type);
CREATE INDEX IF NOT EXISTS idx_user_achievements_unlocked_at ON user_achievements(unlocked_at);

-- 유니크 제약조건 (사용자당 업적 타입별로 하나만)
ALTER TABLE user_achievements ADD CONSTRAINT uk_user_achievement UNIQUE (user_id, achievement_type);

-- 업적 타입 체크 제약조건 (ENUM 값들)
ALTER TABLE user_achievements ADD CONSTRAINT chk_achievement_type 
CHECK (achievement_type IN (
    'FIRST_MISSION',
    'MISSIONS_10',
    'MISSIONS_50', 
    'MISSIONS_100',
    'MISSION_STREAK_3',
    'MISSION_STREAK_7',
    'MISSION_STREAK_30',
    'POINTS_1000',
    'POINTS_10000',
    'HEALTH_SPECIALIST',
    'CREATIVE_ARTIST',
    'SOCIAL_BUTTERFLY'
));

-- 진행도 체크 제약조건 (0-100)
ALTER TABLE user_achievements ADD CONSTRAINT chk_progress 
CHECK (progress >= 0 AND progress <= 100);