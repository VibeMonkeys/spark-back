package com.monkeys.spark.domain.vo.reward

import java.time.LocalDateTime

// 리워드 관련 Value Objects
@JvmInline
value class RewardTitle(val value: String) {
    init {
        require(value.isNotBlank()) { "Reward title cannot be blank" }
        require(value.length <= 100) { "Reward title cannot exceed 100 characters" }
    }
}

@JvmInline
value class RewardDescription(val value: String) {
    init {
        require(value.isNotBlank()) { "Reward description cannot be blank" }
        require(value.length <= 300) { "Reward description cannot exceed 300 characters" }
    }
}

@JvmInline
value class BrandName(val value: String) {
    init {
        require(value.isNotBlank()) { "Brand name cannot be blank" }
        require(value.length <= 50) { "Brand name cannot exceed 50 characters" }
    }
}

@JvmInline
value class OriginalPrice(val value: String) {
    init {
        require(value.isNotBlank()) { "Original price cannot be blank" }
    }
}

@JvmInline
value class DiscountPercentage(val value: Int) {
    init {
        require(value in 0..100) { "Discount percentage must be between 0 and 100" }
    }
}

@JvmInline
value class ExpirationDays(val value: Int) {
    init {
        require(value > 0) { "Expiration days must be positive" }
    }
    
    fun toExpirationDate(from: LocalDateTime = LocalDateTime.now()): LocalDateTime {
        return from.plusDays(value.toLong())
    }
}

enum class RewardCategory(val displayName: String) {
    COFFEE("카페"),
    SHOPPING("쇼핑"),
    ENTERTAINMENT("엔터테인먼트"),
    PREMIUM("프리미엄")
}

enum class RewardStatus {
    AVAILABLE,    // 사용 가능
    USED,         // 사용됨
    EXPIRED       // 만료됨
}