package com.monkeys.spark.domain.vo.mission

// 미션 관련 Value Objects
@JvmInline
value class MissionTitle(val value: String) {
    init {
        require(value.isNotBlank()) { "Mission title cannot be blank" }
        require(value.length <= 100) { "Mission title cannot exceed 100 characters" }
    }
}

@JvmInline
value class MissionDescription(val value: String) {
    init {
        require(value.isNotBlank()) { "Mission description cannot be blank" }
        require(value.length <= 500) { "Mission description cannot exceed 500 characters" }
    }
}

enum class MissionCategory(val displayName: String, val colorClass: String) {
    SOCIAL("사교적", "bg-blue-500"),
    ADVENTURE("모험적", "bg-orange-500"),
    HEALTH("건강", "bg-green-500"),
    CREATIVE("창의적", "bg-purple-500"),
    LEARNING("학습", "bg-yellow-500")
}

enum class MissionDifficulty(val displayName: String, val basePoints: Int, val estimatedMinutes: Int) {
    EASY("Easy", 10, 10),
    MEDIUM("Medium", 20, 20),
    HARD("Hard", 30, 30)
}

enum class MissionStatus {
    ASSIGNED,      // 배정됨
    IN_PROGRESS,   // 진행 중
    COMPLETED,     // 완료
    FAILED,        // 실패
    EXPIRED        // 만료됨
}