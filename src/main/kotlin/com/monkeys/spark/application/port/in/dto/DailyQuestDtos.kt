package com.monkeys.spark.application.port.`in`.dto

import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import com.monkeys.spark.domain.vo.dailyquest.SpecialRewardTier
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 일일 퀘스트 관련 DTO 클래스들
 * "삶을 게임처럼 즐겨라!" - 게임화된 일상 루틴 응답 데이터
 */

/**
 * 일일 퀘스트 기본 정보 DTO
 */
data class DailyQuestDto(
    val id: String,
    val type: DailyQuestType,
    val title: String,
    val description: String,
    val icon: String,
    val order: Int,
    val rewardPoints: Int,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null
)

/**
 * 일일 퀘스트 진행 상황 DTO
 */
data class DailyQuestProgressDto(
    val userId: String,
    val date: LocalDate,
    val quests: List<DailyQuestDto>,
    val completedCount: Int,
    val totalCount: Int,
    val completionPercentage: Int,
    val statusMessage: String
)

/**
 * 일일 퀘스트 요약 DTO (게임화된 현황)
 */
data class DailyQuestSummaryDto(
    val userId: String,
    val date: LocalDate,
    val completedCount: Int,
    val totalCount: Int,
    val completionPercentage: Int,
    val baseRewardPoints: Int,
    val specialRewardPoints: Int,
    val totalRewardPoints: Int,
    val totalStatReward: Int,
    val specialRewardsEarned: List<SpecialRewardTier>,
    val statusMessage: String,
    val nextMilestone: Int?,
    val isAllCompleted: Boolean
)

/**
 * 특수 보상 정보 DTO
 */
data class SpecialRewardDto(
    val tier: SpecialRewardTier,
    val requiredPercentage: Int,
    val pointReward: Int,
    val description: String,
    val emoji: String,
    val isEarned: Boolean
)

/**
 * 일일 퀘스트 통계 DTO
 */
data class DailyQuestStatsDto(
    val userId: String,
    val totalDays: Int,
    val perfectDays: Int,
    val consecutivePerfectDays: Int,
    val averageCompletionRate: Double,
    val totalQuestsCompleted: Int,
    val totalSpecialRewards: Map<SpecialRewardTier, Int>,
    val questTypeStats: Map<DailyQuestType, Int>,
    val improvementTrend: String
)

/**
 * 월별 일일 퀘스트 통계 DTO
 */
data class MonthlyDailyQuestStatsDto(
    val userId: String,
    val year: Int,
    val month: Int,
    val totalDays: Int,
    val completedDays: Int,
    val perfectDays: Int,
    val averageCompletionRate: Double,
    val questTypeStats: Map<DailyQuestType, Int>,
    val specialRewardsEarned: Map<SpecialRewardTier, Int>,
    val dailyCompletionRates: List<DailyCompletionRateDto>
)

/**
 * 일별 완료율 DTO
 */
data class DailyCompletionRateDto(
    val date: LocalDate,
    val completionRate: Int,
    val isCompleted: Boolean
)

/**
 * 일일 퀘스트 리더보드 DTO
 */
data class DailyQuestLeaderboardDto(
    val date: LocalDate,
    val rankings: List<DailyQuestRankingDto>
)

/**
 * 일일 퀘스트 순위 DTO
 */
data class DailyQuestRankingDto(
    val rank: Int,
    val userId: String,
    val userName: String,
    val completionRate: Int,
    val totalRewardPoints: Int,
    val statusMessage: String
)

/**
 * 전체 완료 분포 DTO
 */
data class CompletionDistributionDto(
    val date: LocalDate,
    val totalUsers: Long,
    val distribution: Map<Int, Long>, // completionRate -> userCount
    val averageCompletionRate: Double
)