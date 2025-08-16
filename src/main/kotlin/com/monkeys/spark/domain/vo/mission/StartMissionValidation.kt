package com.monkeys.spark.domain.vo.mission

/**
 * 미션 시작 가능 여부 검증 결과
 */
data class StartMissionValidation(
    val canStart: Boolean,
    val hasOngoingMission: Boolean,
    val todayStartedCount: Long,
    val dailyLimit: DailyMissionLimit,
    val errorCode: String? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun success(todayCount: Long = 0): StartMissionValidation {
            val dailyLimit = DailyMissionLimit.create(todayCount)
            return StartMissionValidation(
                canStart = true,
                hasOngoingMission = false,
                todayStartedCount = todayCount,
                dailyLimit = dailyLimit
            )
        }

        fun dailyLimitExceeded(count: Long): StartMissionValidation {
            val dailyLimit = DailyMissionLimit.create(count)
            return StartMissionValidation(
                canStart = false,
                hasOngoingMission = false,
                todayStartedCount = count,
                dailyLimit = dailyLimit,
                errorCode = "DAILY_LIMIT_EXCEEDED",
                errorMessage = "오늘 시작할 수 있는 미션 수를 초과했습니다. (${count}/${DailyMissionLimit.MAX_DAILY_STARTS})"
            )
        }

        fun allowedToStart(todayCount: Long): StartMissionValidation {
            val dailyLimit = DailyMissionLimit.create(todayCount)
            return StartMissionValidation(
                canStart = true,
                hasOngoingMission = false,
                todayStartedCount = todayCount,
                dailyLimit = dailyLimit
            )
        }
    }
}