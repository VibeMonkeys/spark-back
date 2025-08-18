package com.monkeys.spark.domain.vo.hashtag

/**
 * 해시태그 카테고리 분류
 */
enum class HashtagCategory(val displayName: String, val color: String) {
    HEALTH("건강", "#10B981"),
    FOOD("음식", "#F59E0B"),
    ADVENTURE("여행/모험", "#EF4444"),
    SOCIAL("소셜", "#3B82F6"),
    LEARNING("학습", "#8B5CF6"),
    CREATIVE("창의", "#EC4899"),
    DAILY("일상", "#6B7280"),
    OTHER("기타", "#9CA3AF");

    companion object {
        fun fromStringOrNull(value: String?): HashtagCategory? {
            return if (value.isNullOrBlank()) {
                null
            } else {
                try {
                    valueOf(value.trim().uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }

        fun parseMultiple(categories: String?): Set<HashtagCategory> {
            return categories?.split(",")
                ?.mapNotNull { fromStringOrNull(it) }
                ?.toSet() ?: emptySet()
        }
    }
}