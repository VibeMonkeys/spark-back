package com.monkeys.spark.domain.exception

/**
 * 미션 관련 예외들
 */

/**
 * 미션을 찾을 수 없을 때 발생하는 예외
 */
class MissionNotFoundException(missionId: String) : EntityNotFoundException("Mission", missionId, "MISSION_NOT_FOUND")

/**
 * 미션이 사용자에게 할당되지 않은 경우
 */
class MissionNotAssignedException(missionId: String) : DomainException(
    "Mission '$missionId' is not assigned to user",
    "MISSION_NOT_ASSIGNED"
)

/**
 * 미션이 이미 완료된 경우
 */
class MissionAlreadyCompletedException(missionId: String) : DomainException(
    "Mission '$missionId' is already completed",
    "MISSION_ALREADY_COMPLETED"
)

/**
 * 일일 미션 제한 초과
 */
class DailyLimitExceededException(limit: Int) : DomainException(
    "Daily mission limit of $limit exceeded",
    "DAILY_LIMIT_EXCEEDED"
)

/**
 * 미션이 이미 진행 중인 경우
 */
class MissionInProgressException(missionId: String) : DomainException(
    "Mission '$missionId' is already in progress",
    "MISSION_IN_PROGRESS"
)