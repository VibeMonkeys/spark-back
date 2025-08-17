package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.HashtagStatsRepository
import com.monkeys.spark.application.port.out.HashtagStatsUpdateRepository
import com.monkeys.spark.application.port.out.HashtagStatsSummary
import com.monkeys.spark.domain.model.HashtagStats
import com.monkeys.spark.domain.service.HashtagCategory
import com.monkeys.spark.domain.vo.common.HashtagStatsId
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.HashtagStatsEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.HashtagStatsPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.HashtagStatsJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 해시태그 통계 퍼시스턴스 어댑터
 */
@Component
class HashtagStatsPersistenceAdapter(
    private val hashtagStatsJpaRepository: HashtagStatsJpaRepository,
    private val hashtagStatsMapper: HashtagStatsPersistenceMapper
) : HashtagStatsRepository, HashtagStatsUpdateRepository {

    override fun save(hashtagStats: HashtagStats): HashtagStats {
        val entity = hashtagStatsMapper.toEntity(hashtagStats)
        val savedEntity = hashtagStatsJpaRepository.save(entity)
        return hashtagStatsMapper.toDomain(savedEntity)
    }

    override fun findById(id: HashtagStatsId): HashtagStats? {
        return hashtagStatsJpaRepository.findById(id.value)
            .map { hashtagStatsMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByHashtagAndDate(hashtag: HashTag, date: LocalDate): HashtagStats? {
        return hashtagStatsJpaRepository.findByHashtagAndDate(hashtag.value, date)
            ?.let { hashtagStatsMapper.toDomain(it) }
    }

    override fun findByHashtagOrderByDateDesc(hashtag: HashTag): List<HashtagStats> {
        return hashtagStatsJpaRepository.findByHashtagOrderByDateDesc(hashtag.value)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findTrendingHashtags(date: LocalDate, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findTrendingHashtags(date, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findPopularHashtags(date: LocalDate, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findPopularHashtags(date, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findHashtagsStartingWith(prefix: String, date: LocalDate, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findByHashtagStartingWithOrderByTotalCountDesc(prefix, date, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findByHashtagAndDateBetween(hashtag: HashTag, startDate: LocalDate, endDate: LocalDate): List<HashtagStats> {
        return hashtagStatsJpaRepository.findByHashtagAndDateBetween(hashtag.value, startDate, endDate)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findByCategoryKeywords(date: LocalDate, keywords: List<String>, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        // 최대 3개 키워드까지 지원
        val keyword1 = keywords.getOrNull(0) ?: ""
        val keyword2 = keywords.getOrNull(1) ?: ""
        val keyword3 = keywords.getOrNull(2) ?: ""
        
        return hashtagStatsJpaRepository.findByCategoryKeywords(date, keyword1, keyword2, keyword3, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findByDateOrderByTrendScore(date: LocalDate, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findByDateOrderByTrendScoreDesc(date, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findByDateOrderByDailyCount(date: LocalDate, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findByDateOrderByDailyCountDesc(date, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun getStatsSummary(date: LocalDate): HashtagStatsSummary {
        val result = hashtagStatsJpaRepository.getStatsSummary(date)
        return HashtagStatsSummary(
            totalHashtags = if (result.isNotEmpty()) (result[0] as? Number)?.toLong() ?: 0L else 0L,
            totalDailyUsage = if (result.size > 1) (result[1] as? Number)?.toLong() ?: 0L else 0L,
            averageTrendScore = if (result.size > 2) (result[2] as? Number)?.toDouble() ?: 0.0 else 0.0
        )
    }

    @Transactional
    override fun resetDailyCountsForDate(date: LocalDate) {
        hashtagStatsJpaRepository.resetDailyCountsForDate(date)
    }

    @Transactional
    override fun resetWeeklyCountsForDate(date: LocalDate) {
        hashtagStatsJpaRepository.resetWeeklyCountsForDate(date)
    }

    @Transactional
    override fun resetMonthlyCountsForDate(date: LocalDate) {
        hashtagStatsJpaRepository.resetMonthlyCountsForDate(date)
    }

    override fun findPreviousWeekStats(previousWeekDate: LocalDate, limit: Int): List<HashtagStats> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findPreviousWeekStats(previousWeekDate, pageable)
            .map { hashtagStatsMapper.toDomain(it) }
    }

    override fun findAllHashtagsFromLatestDate(limit: Int): List<String> {
        val pageable = PageRequest.of(0, limit)
        return hashtagStatsJpaRepository.findAllHashtagsFromLatestDate(pageable)
    }

    override fun deleteById(id: HashtagStatsId) {
        hashtagStatsJpaRepository.deleteById(id.value)
    }

    @Transactional
    override fun deleteOldStats(beforeDate: LocalDate) {
        // 특정 날짜 이전의 오래된 통계를 삭제하는 쿼리
        // 현재는 직접 삭제 로직을 구현하지 않고, 필요시 배치 작업으로 처리
        // hashtagStatsJpaRepository.deleteByDateBefore(beforeDate)
    }

    // HashtagStatsUpdateRepository 구현

    override fun incrementHashtagUsage(hashtag: HashTag, date: LocalDate): HashtagStats {
        val updatedRows = hashtagStatsJpaRepository.incrementHashtagUsage(hashtag.value, date)
        
        if (updatedRows > 0) {
            // 트렌드 스코어 재계산
            val entity = hashtagStatsJpaRepository.findByHashtagAndDate(hashtag.value, date)
            entity?.let {
                val newTrendScore = calculateTrendScore(
                    dailyCount = it.dailyCount,
                    weeklyCount = it.weeklyCount,
                    monthlyCount = it.monthlyCount,
                    totalCount = it.totalCount,
                    lastUsedAt = it.lastUsedAt
                )
                hashtagStatsJpaRepository.updateTrendScore(hashtag.value, date, newTrendScore)
                
                // 업데이트된 엔티티 조회 후 반환
                val updatedEntity = hashtagStatsJpaRepository.findByHashtagAndDate(hashtag.value, date)!!
                return hashtagStatsMapper.toDomain(updatedEntity)
            }
        }
        
        throw IllegalStateException("Failed to increment hashtag usage for ${hashtag.value}")
    }

    override fun incrementHashtagsUsage(hashtags: List<HashTag>, date: LocalDate): List<HashtagStats> {
        return hashtags.map { hashtag ->
            if (existsByHashtagAndDate(hashtag, date)) {
                incrementHashtagUsage(hashtag, date)
            } else {
                createOrUpdateHashtagStats(hashtag, date)
            }
        }
    }

    override fun existsByHashtagAndDate(hashtag: HashTag, date: LocalDate): Boolean {
        return hashtagStatsJpaRepository.existsByHashtagAndDate(hashtag.value, date)
    }

    override fun createOrUpdateHashtagStats(hashtag: HashTag, date: LocalDate): HashtagStats {
        // 기존 통계가 있으면 증가, 없으면 새로 생성
        return if (existsByHashtagAndDate(hashtag, date)) {
            incrementHashtagUsage(hashtag, date)
        } else {
            createNewHashtagStats(hashtag, date)
        }
    }
    
    private fun createNewHashtagStats(hashtag: HashTag, date: LocalDate): HashtagStats {
        val category = categorizeHashtag(hashtag.value)
        val trendScore = calculateTrendScore(
            dailyCount = 1,
            weeklyCount = 1,
            monthlyCount = 1,
            totalCount = 1,
            lastUsedAt = LocalDateTime.now()
        )
        
        val newEntity = HashtagStatsEntity().apply {
            id = HashtagStatsId.generate().value
            this.hashtag = hashtag.value
            this.date = date
            dailyCount = 1
            weeklyCount = 1
            monthlyCount = 1
            totalCount = 1
            this.trendScore = trendScore
            lastUsedAt = LocalDateTime.now()
        }
        
        val savedEntity = hashtagStatsJpaRepository.save(newEntity)
        return hashtagStatsMapper.toDomain(savedEntity)
    }
    
    /**
     * 해시태그 카테고리 분류 (임시 구현)
     */
    private fun categorizeHashtag(hashtag: String): HashtagCategory {
        val normalized = hashtag.lowercase()
        return when {
            normalized.contains("건강") || normalized.contains("운동") || normalized.contains("헬스") || 
            normalized.contains("요가") || normalized.contains("다이어트") -> HashtagCategory.HEALTH
            normalized.contains("음식") || normalized.contains("카페") || normalized.contains("커피") || 
            normalized.contains("맛집") || normalized.contains("요리") || normalized.contains("맛있") -> HashtagCategory.FOOD
            normalized.contains("여행") || normalized.contains("모험") || normalized.contains("산") || 
            normalized.contains("바다") || normalized.contains("공원") || normalized.contains("나들이") -> HashtagCategory.ADVENTURE
            normalized.contains("친구") || normalized.contains("가족") || normalized.contains("모임") || 
            normalized.contains("파티") || normalized.contains("만남") -> HashtagCategory.SOCIAL
            normalized.contains("공부") || normalized.contains("책") || normalized.contains("영어") || 
            normalized.contains("학습") || normalized.contains("교육") -> HashtagCategory.LEARNING
            normalized.contains("그림") || normalized.contains("사진") || normalized.contains("창작") || 
            normalized.contains("음악") || normalized.contains("예술") -> HashtagCategory.CREATIVE
            normalized.contains("일상") || normalized.contains("오늘") || normalized.contains("하루") || 
            normalized.contains("주말") || normalized.contains("시간") -> HashtagCategory.DAILY
            else -> HashtagCategory.OTHER
        }
    }
    
    /**
     * 트렌드 스코어 계산 (임시 구현)
     */
    private fun calculateTrendScore(
        dailyCount: Int,
        weeklyCount: Int,
        monthlyCount: Int,
        totalCount: Int,
        lastUsedAt: LocalDateTime
    ): Double {
        val dailyWeight = 0.4
        val weeklyWeight = 0.3
        val monthlyWeight = 0.2
        val recentnessWeight = 0.1
        
        // 최근 사용도 (24시간 이내면 1.0, 그 이후는 감소)
        val hoursSinceLastUsed = java.time.Duration.between(lastUsedAt, LocalDateTime.now()).toHours()
        val recentnessScore = when {
            hoursSinceLastUsed <= 24 -> 1.0
            hoursSinceLastUsed <= 168 -> 0.5 // 1주일
            else -> 0.1
        }
        
        return (dailyCount * dailyWeight + 
                weeklyCount * weeklyWeight + 
                monthlyCount * monthlyWeight + 
                recentnessScore * recentnessWeight) * 10
    }
}