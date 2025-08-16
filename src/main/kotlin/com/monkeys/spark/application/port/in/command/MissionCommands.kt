package com.monkeys.spark.application.port.`in`.command


/**
 * 미션 관련 Command 객체들
 */

/**
 * 미션 시작 커맨드
 */
data class StartMissionCommand(
    val missionId: Long,
    val userId: Long
)

/**
 * 진행도 업데이트 커맨드
 */
data class UpdateProgressCommand(
    val missionId: Long,
    val userId: Long,
    val progress: Int
)

/**
 * 미션 완료 커맨드
 */
data class CompleteMissionCommand(
    val missionId: Long,
    val userId: Long
)

/**
 * 미션 포기 커맨드
 */
data class AbandonMissionCommand(
    val missionId: Long,
    val userId: Long
)

