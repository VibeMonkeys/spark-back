package com.monkeys.spark.domain.vo.achievement

/**
 * 업적 타입 정의
 * 각 업적의 고유 식별자, 이름, 설명, 아이콘, 색상, 조건을 정의
 */
enum class AchievementType(
    val id: String,
    val displayName: String,
    val description: String,
    val icon: String,
    val color: String,
    val category: AchievementCategory,
    val rarity: AchievementRarity
) {
    // 미션 완료 관련 업적
    FIRST_MISSION(
        "first_mission", 
        "첫 걸음", 
        "첫 번째 미션을 완료했습니다", 
        "🌟", 
        "#60A5FA", 
        AchievementCategory.MISSION, 
        AchievementRarity.COMMON
    ),
    
    MISSION_STREAK_3(
        "mission_streak_3", 
        "꾸준함", 
        "3일 연속 미션을 완료했습니다", 
        "🔥", 
        "#F97316", 
        AchievementCategory.STREAK, 
        AchievementRarity.COMMON
    ),
    
    MISSION_STREAK_7(
        "mission_streak_7", 
        "일주일 챌린저", 
        "7일 연속 미션을 완료했습니다", 
        "⚡", 
        "#EAB308", 
        AchievementCategory.STREAK, 
        AchievementRarity.RARE
    ),
    
    MISSION_STREAK_30(
        "mission_streak_30", 
        "한 달 마라토너", 
        "30일 연속 미션을 완료했습니다", 
        "👑", 
        "#DC2626", 
        AchievementCategory.STREAK, 
        AchievementRarity.LEGENDARY
    ),
    
    MISSIONS_10(
        "missions_10", 
        "초보 모험가", 
        "총 10개의 미션을 완료했습니다", 
        "🎯", 
        "#10B981", 
        AchievementCategory.MISSION, 
        AchievementRarity.COMMON
    ),
    
    MISSIONS_50(
        "missions_50", 
        "숙련된 모험가", 
        "총 50개의 미션을 완료했습니다", 
        "🏆", 
        "#8B5CF6", 
        AchievementCategory.MISSION, 
        AchievementRarity.RARE
    ),
    
    MISSIONS_100(
        "missions_100", 
        "전설의 모험가", 
        "총 100개의 미션을 완료했습니다", 
        "💎", 
        "#EC4899", 
        AchievementCategory.MISSION, 
        AchievementRarity.EPIC
    ),
    
    // 포인트 관련 업적
    POINTS_1000(
        "points_1000", 
        "포인트 수집가", 
        "총 1,000포인트를 획득했습니다", 
        "💰", 
        "#06B6D4", 
        AchievementCategory.POINTS, 
        AchievementRarity.COMMON
    ),
    
    POINTS_10000(
        "points_10000", 
        "포인트 마스터", 
        "총 10,000포인트를 획득했습니다", 
        "💳", 
        "#3B82F6", 
        AchievementCategory.POINTS, 
        AchievementRarity.EPIC
    ),
    
    // 카테고리별 특화 업적
    HEALTH_SPECIALIST(
        "health_specialist", 
        "건강 지킴이", 
        "건강 카테고리 미션을 10회 완료했습니다", 
        "💪", 
        "#10B981", 
        AchievementCategory.SPECIALIST, 
        AchievementRarity.RARE
    ),
    
    CREATIVE_ARTIST(
        "creative_artist", 
        "창조적 예술가", 
        "창의적 카테고리 미션을 10회 완료했습니다", 
        "🎨", 
        "#8B5CF6", 
        AchievementCategory.SPECIALIST, 
        AchievementRarity.RARE
    ),
    
    SOCIAL_BUTTERFLY(
        "social_butterfly", 
        "사교적 나비", 
        "사교적 카테고리 미션을 10회 완료했습니다", 
        "🦋", 
        "#EC4899", 
        AchievementCategory.SPECIALIST, 
        AchievementRarity.RARE
    )
}

/**
 * 업적 카테고리
 */
enum class AchievementCategory(
    val displayName: String
) {
    MISSION("미션"),
    STREAK("연속 달성"),
    POINTS("포인트"),
    SPECIALIST("전문가"),
    SPECIAL("특별")
}

/**
 * 업적 희귀도
 */
enum class AchievementRarity(
    val displayName: String,
    val color: String,
    val order: Int
) {
    COMMON("일반", "#9CA3AF", 1),
    RARE("레어", "#3B82F6", 2),
    EPIC("에픽", "#8B5CF6", 3),
    LEGENDARY("전설", "#F59E0B", 4),
    MYTHIC("신화", "#DC2626", 5)
}