package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.HashtagStats
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.domain.vo.hashtag.PopularityThreshold
import com.monkeys.spark.domain.vo.hashtag.HashtagLifecycle
import com.monkeys.spark.domain.vo.hashtag.CategoryAggregation
import com.monkeys.spark.domain.vo.hashtag.HashtagCategory
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 해시태그 통계 도메인 서비스
 * 해시태그 사용량 통계 관리와 관련된 순수 비즈니스 로직을 담당
 */
class HashtagStatsDomainService {
    
    /**
     * 스토리 생성 시 해시태그 통계 업데이트
     */
    fun updateHashtagStatsFromStory(
        story: Story,
        existingStats: Map<HashTag, HashtagStats?>,
        date: LocalDate = LocalDate.now()
    ): List<HashtagStats> {
        val allHashtags = extractAllHashtagsFromStory(story)
        val updatedStats = mutableListOf<HashtagStats>()
        
        allHashtags.forEach { hashtag ->
            val stats = existingStats[hashtag]?.apply {
                // 기존 통계가 있으면 사용량 증가
                incrementUsage()
            } ?: run {
                // 새로운 해시태그면 통계 생성
                HashtagStats.create(hashtag, date)
            }
            
            updatedStats.add(stats)
        }
        
        return updatedStats
    }
    
    /**
     * 해시태그 통계 일괄 업데이트
     * 주기적으로 실행되는 배치 작업에서 사용
     */
    fun batchUpdateHashtagStats(
        hashtagUsageCount: Map<HashTag, Int>,
        existingStats: Map<HashTag, HashtagStats?>,
        date: LocalDate = LocalDate.now()
    ): List<HashtagStats> {
        val updatedStats = mutableListOf<HashtagStats>()
        
        hashtagUsageCount.forEach { (hashtag, count) ->
            val stats = existingStats[hashtag]?.apply {
                // 사용량만큼 증가
                repeat(count) { incrementUsage() }
            } ?: run {
                // 새로운 해시태그면 통계 생성
                HashtagStats.create(hashtag, date).apply {
                    // 초기 카운트를 설정
                    dailyCount = count
                    weeklyCount = count
                    monthlyCount = count
                    totalCount = count
                    updateTrendScore()
                }
            }
            
            updatedStats.add(stats)
        }
        
        return updatedStats
    }
    
    /**
     * 트렌드 스코어 재계산
     * 모든 해시태그의 트렌드 스코어를 다시 계산
     */
    fun recalculateTrendScores(stats: List<HashtagStats>): List<HashtagStats> {
        return stats.map { stat ->
            stat.apply {
                // 트렌드 스코어 재계산 로직은 HashtagStats 모델 내부에 구현됨
                updateTrendScore()
            }
        }
    }
    
    /**
     * 인기 해시태그 식별
     * 주어진 통계 목록에서 인기 해시태그들을 필터링
     */
    fun identifyPopularHashtags(
        stats: List<HashtagStats>,
        popularityThreshold: PopularityThreshold = PopularityThreshold.MODERATE
    ): List<HashtagStats> {
        return stats.filter { stat ->
            when (popularityThreshold) {
                PopularityThreshold.LOW -> {
                    stat.dailyCount >= 3 || stat.weeklyCount >= 15 || stat.trendScore >= 5.0
                }
                PopularityThreshold.MODERATE -> {
                    stat.dailyCount >= 10 || stat.weeklyCount >= 50 || stat.trendScore >= 10.0
                }
                PopularityThreshold.HIGH -> {
                    stat.dailyCount >= 25 || stat.weeklyCount >= 100 || stat.trendScore >= 25.0
                }
            }
        }.sortedByDescending { it.trendScore }
    }
    
    /**
     * 트렌딩 해시태그 식별
     * 급상승하는 해시태그들을 식별
     */
    fun identifyTrendingHashtags(
        stats: List<HashtagStats>,
        previousStats: Map<HashTag, HashtagStats> = emptyMap()
    ): List<HashtagStats> {
        return stats.filter { stat ->
            // 기본 트렌딩 조건
            val isTrending = stat.isTrending()
            
            // 이전 통계와 비교하여 성장률 확인
            val previousStat = previousStats[stat.hashtag]
            val hasGrowth = previousStat?.let { prev ->
                val growthRate = stat.calculateGrowthRate(prev.weeklyCount)
                growthRate > 50.0 // 50% 이상 성장
            } ?: true // 새로운 해시태그는 성장으로 간주
            
            isTrending && hasGrowth
        }.sortedByDescending { it.trendScore }
    }
    
    /**
     * 해시태그 카테고리별 통계 집계
     */
    fun aggregateStatsByCategory(
        stats: List<HashtagStats>,
        hashtagSearchService: HashtagSearchDomainService
    ): Map<HashtagCategory, CategoryAggregation> {
        val categoryMap = mutableMapOf<HashtagCategory, MutableList<HashtagStats>>()
        
        // 카테고리별로 그룹화
        stats.forEach { stat ->
            val category = hashtagSearchService.categorizeHashtag(stat.hashtag)
            categoryMap.getOrPut(category) { mutableListOf() }.add(stat)
        }
        
        // 카테고리별 집계 정보 생성
        return categoryMap.mapValues { (_, categoryStats) ->
            CategoryAggregation(
                totalHashtags = categoryStats.size,
                totalUsage = categoryStats.sumOf { it.totalCount },
                averageTrendScore = categoryStats.map { it.trendScore }.average(),
                topHashtags = categoryStats.sortedByDescending { it.trendScore }.take(5)
            )
        }
    }
    
    /**
     * 해시태그 생명주기 분석
     * 해시태그의 생명주기 단계를 판단
     */
    fun analyzeHashtagLifecycle(
        stats: HashtagStats,
        historicalData: List<HashtagStats> = emptyList()
    ): HashtagLifecycle {
        val daysSinceFirstUsed = if (historicalData.isNotEmpty()) {
            val firstUsed = historicalData.minOfOrNull { it.createdAt }?.toLocalDate()
            firstUsed?.let { java.time.temporal.ChronoUnit.DAYS.between(it, LocalDate.now()) } ?: 0
        } else {
            java.time.temporal.ChronoUnit.DAYS.between(stats.createdAt.toLocalDate(), LocalDate.now())
        }
        
        return when {
            daysSinceFirstUsed <= 7 && stats.trendScore >= 15.0 -> HashtagLifecycle.EMERGING
            daysSinceFirstUsed <= 30 && stats.trendScore >= 25.0 -> HashtagLifecycle.TRENDING
            stats.trendScore >= 20.0 && stats.weeklyCount >= 50 -> HashtagLifecycle.MATURE
            stats.dailyCount <= 2 && stats.weeklyCount <= 10 -> HashtagLifecycle.DECLINING
            else -> HashtagLifecycle.STABLE
        }
    }
    
    /**
     * 해시태그 추천 점수 계산
     * 사용자의 관심사와 해시태그 통계를 바탕으로 추천 점수 계산
     */
    fun calculateRecommendationScore(
        stats: HashtagStats,
        userPreferences: Set<HashtagCategory>,
        hashtagSearchService: HashtagSearchDomainService
    ): Double {
        val category = hashtagSearchService.categorizeHashtag(stats.hashtag)
        
        // 사용자 관심사와 일치하는지 확인
        val categoryMatch = if (userPreferences.contains(category)) 1.0 else 0.5
        
        // 인기도 점수 (0.0 ~ 1.0)
        val popularityScore = (stats.trendScore / 50.0).coerceIn(0.0, 1.0)
        
        // 최신성 점수
        val recencyScore = calculateRecencyScore(stats.lastUsedAt)
        
        // 가중 평균
        return (categoryMatch * 0.4) + (popularityScore * 0.4) + (recencyScore * 0.2)
    }
    
    // === Private Helper Methods ===
    
    private fun extractAllHashtagsFromStory(story: Story): Set<HashTag> {
        val allTags = mutableSetOf<HashTag>()
        allTags.addAll(story.userTags)
        allTags.addAll(story.autoTags)
        return allTags
    }
    
    private fun HashtagStats.updateTrendScore() {
        val now = LocalDateTime.now()
        val hoursSinceLastUsed = java.time.Duration.between(lastUsedAt, now).toHours()
        
        val recencyWeight = when {
            hoursSinceLastUsed <= 1 -> 2.0
            hoursSinceLastUsed <= 6 -> 1.5
            hoursSinceLastUsed <= 24 -> 1.0
            hoursSinceLastUsed <= 168 -> 0.5
            else -> 0.1
        }
        
        val frequencyWeight = when {
            dailyCount >= 50 -> 3.0
            dailyCount >= 20 -> 2.5
            dailyCount >= 10 -> 2.0
            dailyCount >= 5 -> 1.5
            else -> 1.0
        }
        
        trendScore = (dailyCount * recencyWeight * frequencyWeight) + 
                    (weeklyCount * 0.3) + 
                    (monthlyCount * 0.1)
    }
    
    private fun calculateRecencyScore(lastUsedAt: LocalDateTime): Double {
        val now = LocalDateTime.now()
        val hoursSinceLastUsed = java.time.Duration.between(lastUsedAt, now).toHours()
        
        return when {
            hoursSinceLastUsed <= 1 -> 1.0
            hoursSinceLastUsed <= 6 -> 0.8
            hoursSinceLastUsed <= 24 -> 0.6
            hoursSinceLastUsed <= 168 -> 0.4
            else -> 0.2
        }
    }
}