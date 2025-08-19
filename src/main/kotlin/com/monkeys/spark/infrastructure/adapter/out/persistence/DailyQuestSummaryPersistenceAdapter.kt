package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.DailyQuestSummaryRepository
import com.monkeys.spark.domain.model.DailyQuestSummary
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.SpecialRewardTier
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.DailyQuestSummaryPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.DailyQuestSummaryJpaRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * 일일 퀘스트 요약 Persistence Adapter
 * "삶을 게임처럼 즐겨라!" - 하루의 퀘스트 전체 현황과 보상 데이터 액세스 구현
 */
@Repository
class DailyQuestSummaryPersistenceAdapter(
    private val jpaRepository: DailyQuestSummaryJpaRepository,
    private val mapper: DailyQuestSummaryPersistenceMapper
) : DailyQuestSummaryRepository {
    
    private val objectMapper = jacksonObjectMapper()
    
    override fun save(summary: DailyQuestSummary): DailyQuestSummary {
        val entity = mapper.toEntity(summary)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }
    
    override fun findByUserIdAndDate(userId: UserId, date: LocalDate): DailyQuestSummary? {
        return jpaRepository.findByUserIdAndSummaryDate(userId.value, date)
            ?.let { mapper.toDomain(it) }
    }
    
    override fun findTodaySummaryByUserId(userId: UserId): DailyQuestSummary? {
        return jpaRepository.findTodaySummaryByUserId(userId.value)
            ?.let { mapper.toDomain(it) }
    }
    
    override fun findByUserIdAndDateRange(
        userId: UserId,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyQuestSummary> {
        val userIdLong = userId.value
        return jpaRepository.findByUserIdAndDateRange(userIdLong, startDate, endDate)
            .let { mapper.toDomainList(it) }
    }
    
    override fun findRecentSummariesByUserId(userId: UserId, days: Int): List<DailyQuestSummary> {
        val userIdLong = userId.value
        return jpaRepository.findRecentSummariesByUserId(userIdLong, days)
            .let { mapper.toDomainList(it) }
    }
    
    override fun findPerfectDaysByUserId(userId: UserId): List<DailyQuestSummary> {
        val userIdLong = userId.value
        return jpaRepository.findPerfectDaysByUserId(userIdLong)
            .let { mapper.toDomainList(it) }
    }
    
    override fun countConsecutivePerfectDays(userId: UserId): Long {
        return jpaRepository.countConsecutivePerfectDays(userId.value)
    }
    
    override fun getMonthlyStats(userId: UserId, year: Int, month: Int): Map<String, Any> {
        val result = jpaRepository.getMonthlyStats(userId.value, year, month)
        
        return mapOf(
            "totalDays" to (result[0] as Number).toInt(),
            "completedDays" to (result[1] as Number).toInt(),
            "perfectDays" to (result[2] as Number).toInt(),
            "averageCompletionRate" to (result[3] as Number).toDouble(),
            "totalRewardPoints" to (result[4] as Number).toInt()
        )
    }
    
    override fun getYearlyStats(userId: UserId, year: Int): Map<String, Any> {
        val result = jpaRepository.getYearlyStats(userId.value, year)
        
        return mapOf(
            "totalDays" to (result[0] as Number).toInt(),
            "completedDays" to (result[1] as Number).toInt(),
            "perfectDays" to (result[2] as Number).toInt(),
            "averageCompletionRate" to (result[3] as Number).toDouble(),
            "totalRewardPoints" to (result[4] as Number).toInt()
        )
    }
    
    override fun getCompletionDistributionByDate(date: LocalDate): Map<Int, Long> {
        val results = jpaRepository.getCompletionDistributionByDate(date)
        return results.associate { result ->
            val percentage = (result[0] as Number).toInt()
            val count = (result[1] as Number).toLong()
            percentage to count
        }
    }
    
    override fun findTopPerformersByDate(date: LocalDate, limit: Int): List<DailyQuestSummary> {
        return jpaRepository.findTopPerformersByDate(date)
            .take(limit)
            .let { mapper.toDomainList(it) }
    }
    
    override fun findTopConsecutivePerfectDaysUsers(limit: Int): List<Pair<UserId, Long>> {
        val results = jpaRepository.findTopConsecutivePerfectDaysUsers()
        return results.take(limit).map { result ->
            val userId = UserId((result[0] as Number).toLong())
            val consecutiveDays = (result[1] as Number).toLong()
            userId to consecutiveDays
        }
    }
    
    override fun countUsersWithRewardTier(rewardTier: SpecialRewardTier, date: LocalDate): Long {
        return jpaRepository.countUsersWithRewardTier(rewardTier.name, date)
    }
    
    override fun findSpecialRewardsByUserIdAndDate(userId: UserId, date: LocalDate): List<SpecialRewardTier> {
        val json = jpaRepository.findSpecialRewardsByUserIdAndDate(userId.value, date) ?: return emptyList()
        
        return try {
            if (json.isBlank() || json == "[]") {
                emptyList()
            } else {
                val rewardNames: List<String> = objectMapper.readValue(json)
                rewardNames.mapNotNull { name ->
                    try {
                        SpecialRewardTier.valueOf(name)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override fun getTotalSpecialRewardStats(userId: UserId): Map<SpecialRewardTier, Long> {
        val jsonResults = jpaRepository.getTotalSpecialRewardStats(userId.value)
        
        val rewardCounts = mutableMapOf<SpecialRewardTier, Long>()
        
        jsonResults.forEach { json ->
            try {
                val rewardNames: List<String> = objectMapper.readValue(json)
                rewardNames.forEach { name ->
                    try {
                        val tier = SpecialRewardTier.valueOf(name)
                        rewardCounts[tier] = rewardCounts.getOrDefault(tier, 0L) + 1L
                    } catch (e: IllegalArgumentException) {
                        // Ignore invalid reward tier names
                    }
                }
            } catch (e: Exception) {
                // Ignore invalid JSON
            }
        }
        
        return rewardCounts
    }
    
    override fun deleteByUserIdAndDate(userId: UserId, date: LocalDate) {
        jpaRepository.deleteByUserIdAndSummaryDate(userId.value, date)
    }
    
    override fun deleteSummariesOlderThan(date: LocalDate): Long {
        return jpaRepository.deleteSummariesOlderThan(date)
    }
    
    override fun calculateAverageCompletionRate(userId: UserId, days: Int): Double {
        val startDate = LocalDate.now().minusDays(days.toLong())
        return jpaRepository.calculateAverageCompletionRate(userId.value, startDate) ?: 0.0
    }
    
    override fun calculateGlobalAverageCompletionRate(date: LocalDate): Double {
        return jpaRepository.calculateGlobalAverageCompletionRate(date) ?: 0.0
    }
    
    override fun analyzeImprovementTrend(userId: UserId, days: Int): String {
        val recentRates = jpaRepository.getRecentCompletionRatesForTrend(userId.value, days)
        
        if (recentRates.size < 2) {
            return "데이터 부족"
        }
        
        val recent = recentRates.take(days / 2).average()
        val previous = recentRates.drop(days / 2).average()
        
        return when {
            recent > previous + 10 -> "📈 큰 향상"
            recent > previous + 5 -> "📊 향상 중"
            recent > previous -> "🔥 조금씩 향상"
            recent == previous -> "➡️ 유지"
            recent > previous - 5 -> "📉 약간 감소"
            else -> "⚠️ 큰 감소"
        }
    }
    
    override fun findDaysWithCompletionRateAbove(
        userId: UserId,
        completionRate: Int,
        days: Int
    ): List<DailyQuestSummary> {
        val startDate = LocalDate.now().minusDays(days.toLong())
        return jpaRepository.findDaysWithCompletionRateAbove(userId.value, completionRate, startDate)
            .let { mapper.toDomainList(it) }
    }
}