package com.monkeys.spark.domain.model

/**
 * 일일 미션 시작 제한 정보
 */
data class DailyMissionLimit(
    val maxDailyStarts: Int = 3, // 하루 최대 시작 가능 횟수
    val currentStarted: Long = 0, // 오늘 시작한 미션 수
    val remainingStarts: Int = maxDailyStarts - currentStarted.toInt(), // 남은 시작 가능 횟수
    val canStart: Boolean = currentStarted < maxDailyStarts, // 시작 가능 여부
    val resetTime: String = "자정" // 제한 초기화 시간
) {
    companion object {
        const val MAX_DAILY_STARTS = 3
        
        fun create(todayStartedCount: Long): DailyMissionLimit {
            return DailyMissionLimit(
                maxDailyStarts = MAX_DAILY_STARTS,
                currentStarted = todayStartedCount,
                remainingStarts = (MAX_DAILY_STARTS - todayStartedCount.toInt()).coerceAtLeast(0),
                canStart = todayStartedCount < MAX_DAILY_STARTS
            )
        }
    }
}