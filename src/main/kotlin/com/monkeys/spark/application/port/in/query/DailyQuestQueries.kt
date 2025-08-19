package com.monkeys.spark.application.port.`in`.query

import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import java.time.LocalDate

/**
 * 일일 퀘스트 관련 Query 클래스들
 * "삶을 게임처럼 즐겨라!" - 일상 게임화 조회 쿼리들
 */

/**
 * 사용자의 오늘 일일 퀘스트 현황 조회
 */
data class GetTodayDailyQuestsQuery(
    val userId: UserId
)

/**
 * 사용자의 특정 날짜 일일 퀘스트 현황 조회
 */
data class GetDailyQuestsByDateQuery(
    val userId: UserId,
    val date: LocalDate
)

/**
 * 사용자의 일일 퀘스트 통계 조회
 */
data class GetDailyQuestStatsQuery(
    val userId: UserId,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

/**
 * 사용자의 연속 완벽한 하루 조회
 */
data class GetConsecutivePerfectDaysQuery(
    val userId: UserId
)

/**
 * 일일 퀘스트 리더보드 조회
 */
data class GetDailyQuestLeaderboardQuery(
    val date: LocalDate = LocalDate.now(),
    val limit: Int = 10
)

/**
 * 사용자의 월별 일일 퀘스트 통계 조회
 */
data class GetMonthlyDailyQuestStatsQuery(
    val userId: UserId,
    val year: Int,
    val month: Int
)

/**
 * 사용자의 연간 일일 퀘스트 통계 조회
 */
data class GetYearlyDailyQuestStatsQuery(
    val userId: UserId,
    val year: Int
)

/**
 * 특정 퀘스트 타입의 완료 통계 조회
 */
data class GetQuestTypeStatsQuery(
    val questType: DailyQuestType,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

/**
 * 전체 사용자의 일일 퀘스트 완료 분포 조회
 */
data class GetGlobalCompletionDistributionQuery(
    val date: LocalDate = LocalDate.now()
)

/**
 * 사용자의 개선 추세 분석 조회
 */
data class GetImprovementTrendQuery(
    val userId: UserId,
    val days: Int = 30
)