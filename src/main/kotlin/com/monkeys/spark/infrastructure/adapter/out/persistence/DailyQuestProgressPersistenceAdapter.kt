package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.DailyQuestProgressRepository
import com.monkeys.spark.domain.model.DailyQuestProgress
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestProgressId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.DailyQuestProgressPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.DailyQuestProgressJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * 일일 퀘스트 진행 상황 Persistence Adapter
 * 사용자별 특정 날짜의 개별 퀘스트 완료 여부 데이터 액세스 구현
 */
@Repository
class DailyQuestProgressPersistenceAdapter(
    private val jpaRepository: DailyQuestProgressJpaRepository,
    private val mapper: DailyQuestProgressPersistenceMapper
) : DailyQuestProgressRepository {
    
    override fun save(progress: DailyQuestProgress): DailyQuestProgress {
        val entity = mapper.toEntity(progress)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }
    
    override fun findById(progressId: DailyQuestProgressId): DailyQuestProgress? {
        val id = progressId.value.toLongOrNull() ?: return null
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun findByUserIdAndDate(userId: UserId, date: LocalDate): List<DailyQuestProgress> {
        return jpaRepository.findByUserIdAndQuestDateOrderByDailyQuestId(userId.value, date)
            .let { mapper.toDomainList(it) }
    }
    
    override fun findByUserIdAndDateAndQuestType(
        userId: UserId,
        date: LocalDate,
        questType: DailyQuestType
    ): DailyQuestProgress? {
        return jpaRepository.findByUserIdAndQuestDateAndQuestType(userId.value, date, questType.name)
            ?.let { mapper.toDomain(it) }
    }
    
    override fun findTodayProgressByUserId(userId: UserId): List<DailyQuestProgress> {
        val userIdLong = userId.value
        return jpaRepository.findTodayProgressByUserId(userIdLong)
            .let { mapper.toDomainList(it) }
    }
    
    override fun findCompletedProgressByUserIdAndDateRange(
        userId: UserId,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyQuestProgress> {
        val userIdLong = userId.value
        return jpaRepository.findCompletedProgressByUserIdAndDateRange(userIdLong, startDate, endDate)
            .let { mapper.toDomainList(it) }
    }
    
    override fun countCompletedByUserIdAndDate(userId: UserId, date: LocalDate): Long {
        return jpaRepository.countCompletedByUserIdAndDate(userId.value, date)
    }
    
    override fun countTodayCompletedByUserId(userId: UserId): Long {
        return jpaRepository.countTodayCompletedByUserId(userId.value)
    }
    
    override fun findRecentCompletionsByUserId(userId: UserId, days: Int): List<DailyQuestProgress> {
        val userIdLong = userId.value
        val startDate = LocalDate.now().minusDays(days.toLong())
        return jpaRepository.findRecentCompletionsByUserId(userIdLong, startDate)
            .let { mapper.toDomainList(it) }
    }
    
    override fun countCompletedByQuestType(questType: DailyQuestType): Long {
        return jpaRepository.countCompletedByQuestType(questType.name)
    }
    
    override fun countCompletedByDate(date: LocalDate): Long {
        return jpaRepository.countCompletedByDate(date)
    }
    
    override fun isQuestCompletedByUserToday(userId: UserId, questType: DailyQuestType): Boolean {
        return jpaRepository.isQuestCompletedByUserToday(userId.value, questType.name)
    }
    
    override fun deleteById(progressId: DailyQuestProgressId) {
        val id = progressId.value.toLong()
        jpaRepository.deleteById(id)
    }
    
    override fun deleteByUserIdAndDate(userId: UserId, date: LocalDate) {
        jpaRepository.deleteByUserIdAndQuestDate(userId.value, date)
    }
    
    override fun deleteProgressesOlderThan(date: LocalDate): Long {
        return jpaRepository.deleteProgressesOlderThan(date)
    }
    
    override fun getMonthlyCompletionStats(userId: UserId, year: Int, month: Int): Map<DailyQuestType, Int> {
        val results = jpaRepository.getMonthlyCompletionStats(userId.value, year, month)
        
        return results.associate { result ->
            val questTypeName = result[0] as String
            val count = (result[1] as Number).toInt()
            DailyQuestType.valueOf(questTypeName) to count
        }
    }
    
    override fun getYearlyCompletionStats(userId: UserId, year: Int): Map<DailyQuestType, Int> {
        val results = jpaRepository.getYearlyCompletionStats(userId.value, year)
        
        return results.associate { result ->
            val questTypeName = result[0] as String
            val count = (result[1] as Number).toInt()
            DailyQuestType.valueOf(questTypeName) to count
        }
    }
}