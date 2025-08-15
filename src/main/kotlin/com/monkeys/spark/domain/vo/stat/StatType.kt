package com.monkeys.spark.domain.vo.stat

/**
 * 스탯 타입 열거형
 * RPG 게임 스타일의 캐릭터 스탯 시스템
 */
enum class StatType(
    val displayName: String,
    val description: String,
    val icon: String,
    val color: String
) {
    STRENGTH(
        displayName = "힘",
        description = "신체적 능력과 지구력을 나타냅니다",
        icon = "💪",
        color = "#EF4444"
    ),
    INTELLIGENCE(
        displayName = "지능",
        description = "학습 능력과 문제 해결 능력을 나타냅니다",
        icon = "🧠",
        color = "#3B82F6"
    ),
    CREATIVITY(
        displayName = "창의력",
        description = "상상력과 예술적 감각을 나타냅니다",
        icon = "🎨",
        color = "#8B5CF6"
    ),
    SOCIABILITY(
        displayName = "사교성",
        description = "대인관계와 소통 능력을 나타냅니다",
        icon = "🤝",
        color = "#10B981"
    ),
    ADVENTUROUS(
        displayName = "모험심",
        description = "새로운 도전과 탐험 정신을 나타냅니다",
        icon = "🗺️",
        color = "#F59E0B"
    ),
    DISCIPLINE(
        displayName = "규율성",
        description = "자제력과 꾸준함을 나타냅니다",
        icon = "🎯",
        color = "#6B7280"
    );

    companion object {
        fun fromMissionCategory(category: String): StatType? = when (category.uppercase()) {
            "HEALTH", "건강" -> STRENGTH
            "LEARNING", "학습", "STUDY" -> INTELLIGENCE
            "CREATIVE", "창의적" -> CREATIVITY
            "SOCIAL", "사교적" -> SOCIABILITY
            "ADVENTURE", "모험적" -> ADVENTUROUS
            else -> DISCIPLINE // 기본값으로 규율성
        }
    }
}