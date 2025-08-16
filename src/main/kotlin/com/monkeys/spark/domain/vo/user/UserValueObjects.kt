package com.monkeys.spark.domain.vo.user

// 사용자 관련 Value Objects
@JvmInline
value class UserName(val value: String) {
    init {
        require(value.isNotBlank()) { "User name cannot be blank" }
        require(value.length in 2..50) { "User name must be between 2 and 50 characters" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
        require(value.length <= 255) { "Email cannot exceed 255 characters" }
    }
}

@JvmInline
value class AvatarUrl(val value: String) {
    init {
        require(value.isNotBlank()) { "Avatar URL cannot be blank" }
    }
}

@JvmInline
value class Level(val value: Int) {
    init {
        require(value >= 1) { "Level must be at least 1" }
    }
    
    fun levelUp(): Level = Level(value + 1)
}

@JvmInline
value class Streak(val value: Int) {
    init {
        require(value >= 0) { "Streak cannot be negative" }
    }
    
    fun increment(): Streak = Streak(value + 1)
    fun reset(): Streak = Streak(0)
}

enum class UserLevelTitle(val displayName: String) {
    BEGINNER("초보자"),           // 1-5
    EXPLORER("탐험가"),           // 6-10
    ADVENTURER("모험가"),         // 11-20
    EXPERT("전문가"),             // 21-30
    MASTER("마스터"),             // 31-40
    GRANDMASTER("그랜드마스터"),   // 41-45
    LEGEND("전설"),               // 46-50
    MYTHIC("신화")                // 50+
}