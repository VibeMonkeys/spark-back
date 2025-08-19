package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.DailyQuestUseCase
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.application.port.`in`.dto.*
import com.monkeys.spark.application.port.out.*
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.domain.exception.*
import com.monkeys.spark.domain.model.*
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.dailyquest.*
import com.monkeys.spark.domain.vo.stat.StatType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 일일 퀘스트 Application Service
 * "삶을 게임처럼 즐겨라!" - 매일의 루틴을 게임화하는 핵심 서비스
 */
@Service
@Transactional
class DailyQuestApplicationService(
    private val dailyQuestRepository: DailyQuestRepository,
    private val dailyQuestProgressRepository: DailyQuestProgressRepository,
    private val dailyQuestSummaryRepository: DailyQuestSummaryRepository,
    private val userRepository: UserRepository,
    private val userStatsRepository: UserStatsRepository,
    private val responseMapper: ResponseMapper
) : DailyQuestUseCase {

    // ===============================================
    // 1. 기본 퀘스트 관리
    // ===============================================

    override fun getTodayDailyQuests(query: GetTodayDailyQuestsQuery): DailyQuestProgressDto {
        val user = findUserById(query.userId)
        val today = LocalDate.now()
        
        // 오늘 진행 상황 조회
        val progresses = dailyQuestProgressRepository.findByUserIdAndDate(query.userId, today)
        
        // 진행 상황이 없으면 초기화
        if (progresses.isEmpty()) {
            initializeDailyQuests(InitializeDailyQuestsCommand(query.userId, today))
            val newProgresses = dailyQuestProgressRepository.findByUserIdAndDate(query.userId, today)
            return convertToProgressDto(query.userId, today, newProgresses)
        }
        
        return convertToProgressDto(query.userId, today, progresses)
    }

    override fun getDailyQuestsByDate(query: GetDailyQuestsByDateQuery): DailyQuestProgressDto {
        val user = findUserById(query.userId)
        val progresses = dailyQuestProgressRepository.findByUserIdAndDate(query.userId, query.date)
        return convertToProgressDto(query.userId, query.date, progresses)
    }

    override fun getDailyQuestSummary(userId: UserId, date: LocalDate): DailyQuestSummaryDto {
        val user = findUserById(userId)
        val summary = dailyQuestSummaryRepository.findByUserIdAndDate(userId, date)
            ?: createEmptySummary(userId, date)
        
        return convertToSummaryDto(summary)
    }

    // ===============================================
    // 2. 퀘스트 완료 처리
    // ===============================================

    override fun completeDailyQuest(command: CompleteDailyQuestCommand): DailyQuestSummaryDto {
        val user = findUserById(command.userId)
        
        // 진행 상황 조회
        val progress = dailyQuestProgressRepository.findByUserIdAndDateAndQuestType(
            command.userId, command.date, command.questType
        ) ?: throw DailyQuestProgressNotFoundException(
            "Progress not found for user ${command.userId.value} on ${command.date} for quest ${command.questType}"
        )
        
        // 퀘스트 완료 처리
        progress.complete()
        dailyQuestProgressRepository.save(progress)
        
        // 요약 업데이트
        val summary = getOrCreateSummary(command.userId, command.date)
        summary.completeQuest(command.questType)
        dailyQuestSummaryRepository.save(summary)
        
        // 사용자에게 보상 지급
        grantRewards(user, summary)
        
        return convertToSummaryDto(summary)
    }

    override fun uncompleteDailyQuest(command: UncompleteDailyQuestCommand): DailyQuestSummaryDto {
        require(command.date == LocalDate.now()) { 
            "Can only uncomplete today's daily quest" 
        }
        
        val user = findUserById(command.userId)
        
        // 진행 상황 조회
        val progress = dailyQuestProgressRepository.findByUserIdAndDateAndQuestType(
            command.userId, command.date, command.questType
        ) ?: throw DailyQuestProgressNotFoundException(
            "Progress not found for user ${command.userId.value} on ${command.date} for quest ${command.questType}"
        )
        
        // 퀘스트 완료 취소
        progress.uncomplete()
        dailyQuestProgressRepository.save(progress)
        
        // 요약 업데이트
        val summary = getOrCreateSummary(command.userId, command.date)
        summary.uncompleteQuest(command.questType)
        dailyQuestSummaryRepository.save(summary)
        
        return convertToSummaryDto(summary)
    }

    // ===============================================
    // 3. 퀘스트 초기화 (스케줄러용)
    // ===============================================

    override fun initializeDailyQuests(command: InitializeDailyQuestsCommand): DailyQuestProgressDto {
        val user = findUserById(command.userId)
        val dailyQuests = dailyQuestRepository.findAllActiveQuests()
        
        // 이미 초기화된 경우 기존 데이터 반환
        val existingProgresses = dailyQuestProgressRepository.findByUserIdAndDate(command.userId, command.date)
        if (existingProgresses.isNotEmpty()) {
            return convertToProgressDto(command.userId, command.date, existingProgresses)
        }
        
        // 새로운 진행 상황 생성
        val progresses = DailyQuestProgress.createForAllQuests(command.userId, dailyQuests, command.date)
        progresses.forEach { dailyQuestProgressRepository.save(it) }
        
        // 요약 생성
        val summary = DailyQuestSummary.create(command.userId, dailyQuests, command.date)
        dailyQuestSummaryRepository.save(summary)
        
        return convertToProgressDto(command.userId, command.date, progresses)
    }

    override fun initializeAllUsersDailyQuests(command: InitializeAllUsersDailyQuestsCommand): Int {
        // 모든 사용자 조회 (배치 처리로 페이징)
        val users = userRepository.findAll(page = 0, size = 1000) // 배치 크기 1000으로 처리
        var initializedCount = 0
        
        users.forEach { user ->
            try {
                initializeDailyQuests(InitializeDailyQuestsCommand(user.id, command.date))
                initializedCount++
            } catch (e: Exception) {
                // 로깅 처리 (개별 사용자 실패가 전체를 막지 않도록)
                println("Failed to initialize daily quests for user ${user.id.value}: ${e.message}")
            }
        }
        
        return initializedCount
    }

    // ===============================================
    // 4. 통계 및 분석
    // ===============================================

    override fun getDailyQuestStats(query: GetDailyQuestStatsQuery): DailyQuestStatsDto {
        val user = findUserById(query.userId)
        
        val summaries = if (query.startDate != null && query.endDate != null) {
            dailyQuestSummaryRepository.findByUserIdAndDateRange(query.userId, query.startDate, query.endDate)
        } else {
            dailyQuestSummaryRepository.findRecentSummariesByUserId(query.userId, 30)
        }
        
        val totalDays = summaries.size
        val perfectDays = summaries.count { it.isAllCompleted() }
        val consecutivePerfectDays = dailyQuestSummaryRepository.countConsecutivePerfectDays(query.userId)
        val averageCompletionRate = if (totalDays > 0) {
            summaries.map { it.getCompletionPercentage().value }.average()
        } else 0.0
        
        val totalSpecialRewards = dailyQuestSummaryRepository.getTotalSpecialRewardStats(query.userId)
        val questTypeStats = dailyQuestProgressRepository.getMonthlyCompletionStats(
            query.userId, LocalDate.now().year, LocalDate.now().monthValue
        )
        val improvementTrend = dailyQuestSummaryRepository.analyzeImprovementTrend(query.userId, 30)
        
        return DailyQuestStatsDto(
            userId = query.userId.value.toString(),
            totalDays = totalDays,
            perfectDays = perfectDays,
            consecutivePerfectDays = consecutivePerfectDays.toInt(),
            averageCompletionRate = averageCompletionRate,
            totalSpecialRewards = totalSpecialRewards.mapValues { it.value.toInt() },
            questTypeStats = questTypeStats,
            improvementTrend = improvementTrend
        )
    }

    override fun getConsecutivePerfectDays(query: GetConsecutivePerfectDaysQuery): Int {
        return dailyQuestSummaryRepository.countConsecutivePerfectDays(query.userId).toInt()
    }

    override fun getMonthlyStats(query: GetMonthlyDailyQuestStatsQuery): MonthlyDailyQuestStatsDto {
        val user = findUserById(query.userId)
        val stats = dailyQuestSummaryRepository.getMonthlyStats(query.userId, query.year, query.month)
        
        // 추가 로직 구현 필요
        return MonthlyDailyQuestStatsDto(
            userId = query.userId.value.toString(),
            year = query.year,
            month = query.month,
            totalDays = stats["totalDays"] as? Int ?: 0,
            completedDays = stats["completedDays"] as? Int ?: 0,
            perfectDays = stats["perfectDays"] as? Int ?: 0,
            averageCompletionRate = stats["averageCompletionRate"] as? Double ?: 0.0,
            questTypeStats = emptyMap(), // 실제 구현 필요
            specialRewardsEarned = emptyMap(), // 실제 구현 필요
            dailyCompletionRates = emptyList() // 실제 구현 필요
        )
    }

    override fun getYearlyStats(query: GetYearlyDailyQuestStatsQuery): Map<String, Any> {
        val user = findUserById(query.userId)
        return dailyQuestSummaryRepository.getYearlyStats(query.userId, query.year)
    }

    override fun getImprovementTrend(query: GetImprovementTrendQuery): String {
        val user = findUserById(query.userId)
        return dailyQuestSummaryRepository.analyzeImprovementTrend(query.userId, query.days)
    }

    // ===============================================
    // 5. 리더보드 및 순위
    // ===============================================

    override fun getDailyQuestLeaderboard(query: GetDailyQuestLeaderboardQuery): DailyQuestLeaderboardDto {
        val topSummaries = dailyQuestSummaryRepository.findTopPerformersByDate(query.date, query.limit)
        
        val rankings = topSummaries.mapIndexed { index, summary ->
            val user = findUserById(summary.userId)
            DailyQuestRankingDto(
                rank = index + 1,
                userId = summary.userId.value.toString(),
                userName = user.name.value,
                completionRate = summary.getCompletionPercentage().value,
                totalRewardPoints = summary.getTotalRewardPoints().value,
                statusMessage = summary.getStatusMessage()
            )
        }
        
        return DailyQuestLeaderboardDto(
            date = query.date,
            rankings = rankings
        )
    }

    override fun getGlobalCompletionDistribution(query: GetGlobalCompletionDistributionQuery): CompletionDistributionDto {
        val distribution = dailyQuestSummaryRepository.getCompletionDistributionByDate(query.date)
        val totalUsers = distribution.values.sum()
        val averageCompletionRate = dailyQuestSummaryRepository.calculateGlobalAverageCompletionRate(query.date)
        
        return CompletionDistributionDto(
            date = query.date,
            totalUsers = totalUsers,
            distribution = distribution,
            averageCompletionRate = averageCompletionRate
        )
    }

    override fun getTopConsecutivePerfectDaysUsers(limit: Int): List<Pair<String, Long>> {
        return dailyQuestSummaryRepository.findTopConsecutivePerfectDaysUsers(limit)
            .map { (userId, days) -> userId.value.toString() to days }
    }

    // ===============================================
    // 6. 관리자 기능 (퀘스트 템플릿 관리)
    // ===============================================

    override fun createDailyQuestTemplate(command: CreateDailyQuestTemplateCommand): DailyQuestDto {
        val dailyQuest = DailyQuest.create(command.questType)
        val savedQuest = dailyQuestRepository.save(dailyQuest)
        return convertToQuestDto(savedQuest)
    }

    override fun updateDailyQuestTemplate(command: UpdateDailyQuestTemplateCommand): DailyQuestDto {
        val existingQuest = dailyQuestRepository.findByType(command.questType)
            ?: throw DailyQuestNotFoundException("Daily quest not found: ${command.questType}")
        
        // 업데이트 로직 구현 (실제로는 도메인 모델에 update 메서드 추가 필요)
        val updatedQuest = dailyQuestRepository.save(existingQuest)
        return convertToQuestDto(updatedQuest)
    }

    override fun getAllDailyQuestTemplates(): List<DailyQuestDto> {
        return dailyQuestRepository.findAll().map { convertToQuestDto(it) }
    }

    // ===============================================
    // 7. 특수 보상 시스템
    // ===============================================

    override fun grantSpecialReward(command: GrantSpecialRewardCommand): List<SpecialRewardDto> {
        val rewardTier = SpecialRewardTier.getRewardForPercentage(command.completionPercentage)
            ?: return emptyList()
        
        val user = findUserById(command.userId)
        user.earnPoints(Points(rewardTier.basePoints))
        userRepository.save(user)
        
        return listOf(SpecialRewardDto(
            tier = rewardTier,
            requiredPercentage = rewardTier.requiredPercentage,
            pointReward = rewardTier.basePoints,
            description = rewardTier.description,
            emoji = rewardTier.emoji,
            isEarned = true
        ))
    }

    override fun getSpecialRewardStats(userId: UserId): Map<String, Any> {
        val user = findUserById(userId)
        return dailyQuestSummaryRepository.getTotalSpecialRewardStats(userId)
            .mapKeys { it.key.name }
            .mapValues { it.value as Any }
    }

    override fun getQuestTypeStats(query: GetQuestTypeStatsQuery): Map<String, Any> {
        val totalCompleted = dailyQuestProgressRepository.countCompletedByQuestType(query.questType)
        
        return mapOf(
            "questType" to query.questType.name,
            "totalCompleted" to totalCompleted,
            "title" to query.questType.title,
            "description" to query.questType.description
        )
    }

    // ===============================================
    // 8. 게임화 요소
    // ===============================================

    override fun getGameifiedStatusMessage(userId: UserId, date: LocalDate): String {
        val summary = dailyQuestSummaryRepository.findByUserIdAndDate(userId, date)
            ?: return "🌅 새로운 하루의 시작! 퀘스트를 완료해보세요."
        
        return summary.getStatusMessage()
    }

    override fun getNextMilestone(userId: UserId, date: LocalDate): SpecialRewardDto? {
        val summary = dailyQuestSummaryRepository.findByUserIdAndDate(userId, date)
            ?: return null
        
        val currentPercentage = summary.getCompletionPercentage()
        val nextMilestone = currentPercentage.getNextMilestone() ?: return null
        val rewardTier = SpecialRewardTier.getRewardForPercentage(nextMilestone) ?: return null
        
        return SpecialRewardDto(
            tier = rewardTier,
            requiredPercentage = rewardTier.requiredPercentage,
            pointReward = rewardTier.basePoints,
            description = rewardTier.description,
            emoji = rewardTier.emoji,
            isEarned = false
        )
    }

    override fun analyzeUserAchievement(userId: UserId, days: Int): Map<String, Any> {
        val user = findUserById(userId)
        val recentSummaries = dailyQuestSummaryRepository.findRecentSummariesByUserId(userId, days)
        
        val totalDays = recentSummaries.size
        val perfectDays = recentSummaries.count { it.isAllCompleted() }
        val averageCompletion = if (totalDays > 0) {
            recentSummaries.map { it.getCompletionPercentage().value }.average()
        } else 0.0
        
        return mapOf(
            "totalDays" to totalDays,
            "perfectDays" to perfectDays,
            "perfectDayRate" to if (totalDays > 0) (perfectDays.toDouble() / totalDays) * 100 else 0.0,
            "averageCompletion" to averageCompletion,
            "improvementTrend" to getImprovementTrend(GetImprovementTrendQuery(userId, days))
        )
    }

    // ===============================================
    // Private Helper Methods
    // ===============================================

    private fun findUserById(userId: UserId): User {
        return userRepository.findById(userId)
            ?: throw UserNotFoundException("User not found: ${userId.value}")
    }

    private fun getOrCreateSummary(userId: UserId, date: LocalDate): DailyQuestSummary {
        return dailyQuestSummaryRepository.findByUserIdAndDate(userId, date)
            ?: createEmptySummary(userId, date)
    }

    private fun createEmptySummary(userId: UserId, date: LocalDate): DailyQuestSummary {
        val dailyQuests = dailyQuestRepository.findAllActiveQuests()
        val progresses = dailyQuestProgressRepository.findByUserIdAndDate(userId, date)
        
        return if (progresses.isNotEmpty()) {
            DailyQuestSummary.fromProgresses(userId, date, progresses)
        } else {
            DailyQuestSummary.create(userId, dailyQuests, date)
        }
    }

    private fun grantRewards(user: User, summary: DailyQuestSummary) {
        // 기본 포인트 지급
        user.earnPoints(Points(5)) // 퀘스트당 5포인트
        
        // 스탯 증가 (규율 스탯)
        val userStats = userStatsRepository.findByUserId(user.id)
        userStats?.let {
            val updatedStats = it.addStatValue(StatType.DISCIPLINE, 1)
            userStatsRepository.save(updatedStats)
        }
        
        // 특수 보상 처리
        summary.getNewSpecialRewards().forEach { rewardTier ->
            user.earnPoints(Points(rewardTier.basePoints))
        }
        
        userRepository.save(user)
    }

    private fun convertToProgressDto(userId: UserId, date: LocalDate, progresses: List<DailyQuestProgress>): DailyQuestProgressDto {
        val questDtos = progresses.map { progress ->
            val dailyQuest = dailyQuestRepository.findById(progress.dailyQuestId)
                ?: throw DailyQuestNotFoundException("Daily quest not found: ${progress.dailyQuestId}")
            
            DailyQuestDto(
                id = progress.id.value,
                type = progress.questType,
                title = dailyQuest.title,
                description = dailyQuest.description,
                icon = dailyQuest.icon,
                order = dailyQuest.order,
                rewardPoints = dailyQuest.rewardPoints.value,
                isCompleted = progress.isCompleted,
                completedAt = progress.completedAt
            )
        }.sortedBy { it.order }
        
        val completedCount = progresses.count { it.isCompleted }
        val totalCount = progresses.size
        val completionPercentage = CompletionPercentage.from(completedCount, totalCount)
        val statusMessage = when (completionPercentage.value) {
            0 -> "🌅 새로운 하루의 시작! 퀘스트를 완료해보세요."
            25 -> "🥉 좋은 시작이에요! 계속해보세요."
            50 -> "🥈 절반 완료! 잘하고 있어요."
            75 -> "🥇 거의 다 왔어요! 마지막 퀘스트까지!"
            100 -> "💎 완벽한 하루! 모든 퀘스트를 완료했습니다!"
            else -> "🔥 진행 중... 삶을 게임처럼 즐겨보세요!"
        }
        
        return DailyQuestProgressDto(
            userId = userId.value.toString(),
            date = date,
            quests = questDtos,
            completedCount = completedCount,
            totalCount = totalCount,
            completionPercentage = completionPercentage.value,
            statusMessage = statusMessage
        )
    }

    private fun convertToSummaryDto(summary: DailyQuestSummary): DailyQuestSummaryDto {
        val completionPercentage = summary.getCompletionPercentage()
        val nextMilestone = completionPercentage.getNextMilestone()
        
        return DailyQuestSummaryDto(
            userId = summary.userId.value.toString(),
            date = summary.date,
            completedCount = summary.getCompletedCount(),
            totalCount = summary.getTotalCount(),
            completionPercentage = completionPercentage.value,
            baseRewardPoints = summary.getBaseRewardPoints().value,
            specialRewardPoints = summary.getSpecialRewardPoints().value,
            totalRewardPoints = summary.getTotalRewardPoints().value,
            totalStatReward = summary.getTotalStatReward(),
            specialRewardsEarned = summary.getNewSpecialRewards(),
            statusMessage = summary.getStatusMessage(),
            nextMilestone = nextMilestone,
            isAllCompleted = summary.isAllCompleted()
        )
    }

    private fun convertToQuestDto(dailyQuest: DailyQuest): DailyQuestDto {
        return DailyQuestDto(
            id = dailyQuest.id.value,
            type = dailyQuest.type,
            title = dailyQuest.title,
            description = dailyQuest.description,
            icon = dailyQuest.icon,
            order = dailyQuest.order,
            rewardPoints = dailyQuest.rewardPoints.value,
            isCompleted = false
        )
    }
}

// Custom Exceptions
class DailyQuestNotFoundException(message: String) : DomainException(message, "DAILY_QUEST_NOT_FOUND")
class DailyQuestProgressNotFoundException(message: String) : DomainException(message, "DAILY_QUEST_PROGRESS_NOT_FOUND")