package com.monkeys.spark.domain.model

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
        
        // 여러 미션 동시 진행을 허용하므로 현재는 사용되지 않음
        // fun hasOngoingMission(count: Long): StartMissionValidation {
        //     val dailyLimit = DailyMissionLimit.create(count)
        //     return StartMissionValidation(
        //         canStart = false,
        //         hasOngoingMission = true,
        //         todayStartedCount = count,
        //         dailyLimit = dailyLimit,
        //         errorCode = "MISSION_IN_PROGRESS",
        //         errorMessage = "이미 진행 중인 미션이 있습니다."
        //     )
        // }
        
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