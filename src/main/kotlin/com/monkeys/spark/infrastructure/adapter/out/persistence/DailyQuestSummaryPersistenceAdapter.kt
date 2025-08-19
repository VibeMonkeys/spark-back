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
        // 기존 엔티티 존재 여부 확인
        val existingEntity = jpaRepository.findByUserIdAndSummaryDate(summary.userId.value, summary.date)
        
        return if (existingEntity != null) {
            // 기존 엔티티가 있으면 네이티브 UPDATE 쿼리 사용
            val entity = mapper.toEntity(summary)
            jpaRepository.updateByUserIdAndDate(
                userId = entity.userId,
                summaryDate = entity.summaryDate,
                completedCount = entity.completedCount,
                totalCount = entity.totalCount,
                completionPercentage = entity.completionPercentage,
                baseRewardPoints = entity.baseRewardPoints,
                specialRewardPoints = entity.specialRewardPoints,
                totalRewardPoints = entity.totalRewardPoints,
                specialRewardsEarned = entity.specialRewardsEarned,
                totalStatReward = entity.totalStatReward,
                statusMessage = entity.statusMessage,
                updatedAt = entity.updatedAt
            )
            
            // UPDATE 후 최신 데이터 조회하여 반환
            val updatedEntity = jpaRepository.findByUserIdAndSummaryDate(summary.userId.value, summary.date)
                ?: throw IllegalStateException("UPDATE 실행 후 엔티티를 찾을 수 없습니다: userId=${summary.userId.value}, date=${summary.date}")
            
            mapper.toDomain(updatedEntity)
        } else {
            // 새로운 엔티티인 경우에만 INSERT
            val entity = mapper.toEntity(summary)
            val savedEntity = jpaRepository.save(entity)
            mapper.toDomain(savedEntity)
        }
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
        
        // JPA 집계 쿼리 결과를 안전하게 처리
        return when (result.size) {
            1 -> {
                // 단일 결과가 배열로 래핑된 경우 (JPA가 배열 내부에 모든 컬럼을 포함)
                val row = result[0] as Array<*>
                mapOf(
                    "totalDays" to ((row[0] as? Number)?.toInt() ?: 0),
                    "completedDays" to ((row[1] as? Number)?.toInt() ?: 0),
                    "perfectDays" to ((row[2] as? Number)?.toInt() ?: 0),
                    "averageCompletionRate" to ((row[3] as? Number)?.toDouble() ?: 0.0),
                    "totalRewardPoints" to ((row[4] as? Number)?.toInt() ?: 0)
                )
            }
            5 -> {
                // 각 컬럼이 별도 요소로 반환된 경우
                mapOf(
                    "totalDays" to ((result[0] as? Number)?.toInt() ?: 0),
                    "completedDays" to ((result[1] as? Number)?.toInt() ?: 0),
                    "perfectDays" to ((result[2] as? Number)?.toInt() ?: 0),
                    "averageCompletionRate" to ((result[3] as? Number)?.toDouble() ?: 0.0),
                    "totalRewardPoints" to ((result[4] as? Number)?.toInt() ?: 0)
                )
            }
            else -> {
                // 빈 결과나 예상하지 못한 구조일 때 기본값 반환
                mapOf(
                    "totalDays" to 0,
                    "completedDays" to 0,
                    "perfectDays" to 0,
                    "averageCompletionRate" to 0.0,
                    "totalRewardPoints" to 0
                )
            }
        }
    }
    
    override fun getYearlyStats(userId: UserId, year: Int): Map<String, Any> {
        val result = jpaRepository.getYearlyStats(userId.value, year)
        
        // JPA 집계 쿼리 결과를 안전하게 처리
        return when (result.size) {
            1 -> {
                // 단일 결과가 배열로 래핑된 경우 (JPA가 배열 내부에 모든 컬럼을 포함)
                val row = result[0] as Array<*>
                mapOf(
                    "totalDays" to ((row[0] as? Number)?.toInt() ?: 0),
                    "completedDays" to ((row[1] as? Number)?.toInt() ?: 0),
                    "perfectDays" to ((row[2] as? Number)?.toInt() ?: 0),
                    "averageCompletionRate" to ((row[3] as? Number)?.toDouble() ?: 0.0),
                    "totalRewardPoints" to ((row[4] as? Number)?.toInt() ?: 0)
                )
            }
            5 -> {
                // 각 컬럼이 별도 요소로 반환된 경우
                mapOf(
                    "totalDays" to ((result[0] as? Number)?.toInt() ?: 0),
                    "completedDays" to ((result[1] as? Number)?.toInt() ?: 0),
                    "perfectDays" to ((result[2] as? Number)?.toInt() ?: 0),
                    "averageCompletionRate" to ((result[3] as? Number)?.toDouble() ?: 0.0),
                    "totalRewardPoints" to ((result[4] as? Number)?.toInt() ?: 0)
                )
            }
            else -> {
                // 빈 결과나 예상하지 못한 구조일 때 기본값 반환
                mapOf(
                    "totalDays" to 0,
                    "completedDays" to 0,
                    "perfectDays" to 0,
                    "averageCompletionRate" to 0.0,
                    "totalRewardPoints" to 0
                )
            }
        }
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