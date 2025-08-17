package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.HashtagSearchUseCase
import com.monkeys.spark.application.port.`in`.command.HashtagSearchCommand
import com.monkeys.spark.application.port.`in`.dto.*
import com.monkeys.spark.application.port.out.HashtagStatsRepository
import com.monkeys.spark.application.port.out.StoryRepository
import com.monkeys.spark.domain.service.HashtagCategory
import com.monkeys.spark.domain.service.HashtagSearchDomainService
import com.monkeys.spark.domain.service.HashtagStatsDomainService
import com.monkeys.spark.domain.vo.story.HashTag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 해시태그 검색 애플리케이션 서비스
 */
@Service
@Transactional(readOnly = true)
class HashtagSearchApplicationService(
    private val hashtagStatsRepository: HashtagStatsRepository,
    private val storyRepository: StoryRepository,
    private val hashtagSearchDomainService: HashtagSearchDomainService,
    private val hashtagStatsDomainService: HashtagStatsDomainService
) : HashtagSearchUseCase {

    override fun searchHashtags(command: HashtagSearchCommand): HashtagSearchResult {
        val normalizedQuery = command.normalizedQuery()
        
        // 기본 검색 수행
        val hashtags = when {
            command.hasCategoryFilter() -> {
                val categoryKeywords = getCategoryKeywords(command.category!!)
                hashtagStatsRepository.findByCategoryKeywords(
                    command.dateTo ?: LocalDate.now(),
                    categoryKeywords,
                    command.limit + 10 // 여유분으로 더 많이 조회 후 필터링
                )
            }
            command.hasDateFilter() -> {
                hashtagStatsRepository.findByDateOrderByTrendScore(
                    command.dateTo!!,
                    command.limit + 10
                )
            }
            else -> {
                // prefix 기반 검색 + 유사도 검색 결합
                val prefixResults = hashtagStatsRepository.findHashtagsStartingWith(
                    normalizedQuery,
                    LocalDate.now(),
                    command.limit
                )
                
                // 만약 prefix 결과가 충분하면 유사도 검색은 하지 않음
                if (prefixResults.size >= command.limit) {
                    prefixResults
                } else {
                    // 부족한 경우에만 유사도 검색 추가 (더 높은 임계값 사용)
                    val similarResults = hashtagStatsRepository.findByDateOrderByTrendScore(
                        LocalDate.now(),
                        command.limit * 2
                    ).filter { stats ->
                        val similarity = hashtagSearchDomainService.calculateHashtagSimilarity(
                            HashTag(normalizedQuery),
                            stats.hashtag
                        )
                        // 임계값을 0.6으로 높여서 더 관련성 높은 결과만 포함
                        similarity >= 0.6 && !prefixResults.any { it.id == stats.id }
                    }
                    
                    (prefixResults + similarResults).distinctBy { it.id }.take(command.limit + 10)
                }
            }
        }
        
        // 도메인 서비스를 통한 검색 결과 정렬
        val sortedHashtags = hashtagSearchDomainService.sortSearchResults(
            hashtags,
            command.query,
            command.sortBy
        )
        
        // 페이징 적용
        val pagedHashtags = sortedHashtags
            .drop(command.offset)
            .take(command.limit)
        
        // DTO 변환
        val hashtagDtos = pagedHashtags.map { stats ->
            toHashtagStatsDto(stats, command.includeStoryCount, command.userId)
        }
        
        // 검색어 제안 생성
        val suggestions = generateSearchSuggestions(command.query, hashtags)
        
        return HashtagSearchResult(
            hashtags = hashtagDtos,
            totalCount = sortedHashtags.size.toLong(),
            hasMore = sortedHashtags.size > command.offset + command.limit,
            searchQuery = command.query,
            suggestions = suggestions
        )
    }

    override fun autocompleteHashtags(prefix: String, limit: Int): List<String> {
        val normalizedPrefix = if (prefix.startsWith("#")) prefix else "#$prefix"
        
        // 더 많은 후보를 가져와서 더 정확한 필터링 수행
        val suggestions = hashtagStatsRepository.findHashtagsStartingWith(
            normalizedPrefix,
            LocalDate.now(),
            limit * 3 // 3배 많이 가져와서 필터링
        )
        
        val filteredSuggestions = hashtagSearchDomainService.filterAutocompleteCandidates(
            prefix,
            suggestions,
            limit
        )
        
        return filteredSuggestions.map { it.hashtag.value }
    }

    override fun getPopularHashtags(date: LocalDate, limit: Int): List<HashtagStatsDto> {
        val popularHashtags = hashtagStatsRepository.findPopularHashtags(date, limit)
        return popularHashtags.map { stats -> toHashtagStatsDto(stats) }
    }

    override fun getTrendingHashtags(date: LocalDate, limit: Int): List<HashtagStatsDto> {
        val trendingHashtags = hashtagStatsRepository.findTrendingHashtags(date, limit)
        return trendingHashtags.map { stats -> toHashtagStatsDto(stats) }
    }

    override fun getHashtagsByCategory(
        category: HashtagCategory,
        date: LocalDate,
        limit: Int
    ): List<HashtagStatsDto> {
        val categoryKeywords = getCategoryKeywords(category)
        val categoryHashtags = hashtagStatsRepository.findByCategoryKeywords(date, categoryKeywords, limit)
        
        return categoryHashtags
            .filter { stats ->
                hashtagSearchDomainService.categorizeHashtag(stats.hashtag) == category
            }
            .map { stats -> toHashtagStatsDto(stats) }
    }

    override fun getHashtagDetails(hashtag: String): HashtagStatsDto? {
        val normalizedHashtag = if (hashtag.startsWith("#")) hashtag else "#$hashtag"
        val hashtagObj = HashTag(normalizedHashtag)
        
        return hashtagStatsRepository.findByHashtagAndDate(hashtagObj, LocalDate.now())
            ?.let { stats -> toHashtagStatsDto(stats, includeStoryCount = true) }
    }

    override fun getHashtagTrends(hashtag: String, days: Int): List<HashtagTrendDto> {
        val normalizedHashtag = if (hashtag.startsWith("#")) hashtag else "#$hashtag"
        val hashtagObj = HashTag(normalizedHashtag)
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong())
        
        val trends = hashtagStatsRepository.findByHashtagAndDateBetween(hashtagObj, startDate, endDate)
        
        return trends.map { stats ->
            val previousDayStats = trends.find { it.date == stats.date.minusDays(1) }
            val growthRate = previousDayStats?.let { prev ->
                if (prev.dailyCount > 0) {
                    ((stats.dailyCount - prev.dailyCount).toDouble() / prev.dailyCount) * 100
                } else {
                    100.0
                }
            } ?: 0.0
            
            HashtagTrendDto(
                date = stats.date,
                dailyCount = stats.dailyCount,
                trendScore = stats.trendScore,
                growthRate = growthRate
            )
        }.sortedBy { it.date }
    }

    override fun getRecommendedHashtags(
        userId: String?,
        preferences: Set<HashtagCategory>,
        limit: Int
    ): List<HashtagStatsDto> {
        // 최근 인기 해시태그 조회
        val recentPopular = hashtagStatsRepository.findPopularHashtags(LocalDate.now(), limit * 2)
        
        // 사용자 선호도 기반 필터링 및 점수 계산
        val recommendedHashtags = recentPopular
            .map { stats ->
                val recommendationScore = hashtagStatsDomainService.calculateRecommendationScore(
                    stats,
                    preferences,
                    hashtagSearchDomainService
                )
                stats to recommendationScore
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
        
        return recommendedHashtags.map { stats -> 
            toHashtagStatsDto(stats).copy(
                recommendationScore = hashtagStatsDomainService.calculateRecommendationScore(
                    stats,
                    preferences,
                    hashtagSearchDomainService
                )
            )
        }
    }

    override fun getHashtagStatsSummary(date: LocalDate): HashtagStatsSummaryDto {
        val summary = hashtagStatsRepository.getStatsSummary(date)
        val allHashtags = hashtagStatsRepository.findByDateOrderByTrendScore(date, 1000)
        
        // 카테고리별 집계
        val categoryAggregation = hashtagStatsDomainService.aggregateStatsByCategory(
            allHashtags,
            hashtagSearchDomainService
        )
        
        val totalHashtagsCount = allHashtags.size
        val topCategories = categoryAggregation.entries
            .sortedByDescending { it.value.totalUsage }
            .take(5)
            .map { (category, aggregation) ->
                CategoryUsageDto(
                    category = category.name,
                    displayName = category.displayName,
                    count = aggregation.totalHashtags,
                    percentage = (aggregation.totalHashtags.toDouble() / totalHashtagsCount) * 100,
                    color = category.color
                )
            }
        
        return HashtagStatsSummaryDto(
            totalHashtags = summary.totalHashtags,
            totalDailyUsage = summary.totalDailyUsage,
            averageTrendScore = summary.averageTrendScore,
            topCategories = topCategories,
            newHashtags = allHashtags.count { it.date == date },
            trendingCount = allHashtags.count { it.isTrending() },
            popularCount = allHashtags.count { it.isPopular() }
        )
    }

    // === Private Helper Methods ===

    private fun toHashtagStatsDto(
        stats: com.monkeys.spark.domain.model.HashtagStats,
        includeStoryCount: Boolean = false,
        userId: String? = null
    ): HashtagStatsDto {
        val category = hashtagSearchDomainService.categorizeHashtag(stats.hashtag)
        val lifecycle = hashtagStatsDomainService.analyzeHashtagLifecycle(stats)
        
        val relatedStoryCount = if (includeStoryCount) {
            // 관련 스토리 수 조회 (임시 구현)
            storyRepository.findByHashTag(stats.hashtag).size
        } else null
        
        return HashtagStatsDto(
            id = stats.id.value,
            hashtag = stats.hashtag.value,
            category = category.name,
            categoryDisplayName = category.displayName,
            categoryColor = category.color,
            dailyCount = stats.dailyCount,
            weeklyCount = stats.weeklyCount,
            monthlyCount = stats.monthlyCount,
            totalCount = stats.totalCount,
            trendScore = stats.trendScore,
            lifecycle = lifecycle.name,
            lifecycleDisplayName = lifecycle.displayName,
            lastUsedAt = stats.lastUsedAt,
            date = stats.date,
            isPopular = stats.isPopular(),
            isTrending = stats.isTrending(),
            relatedStoryCount = relatedStoryCount
        )
    }

    private fun getCategoryKeywords(category: HashtagCategory): List<String> {
        return when (category) {
            HashtagCategory.HEALTH -> listOf("운동", "헬스", "건강")
            HashtagCategory.FOOD -> listOf("음식", "카페", "맛집")
            HashtagCategory.ADVENTURE -> listOf("여행", "모험", "탐험")
            HashtagCategory.SOCIAL -> listOf("친구", "사람", "소셜")
            HashtagCategory.LEARNING -> listOf("학습", "공부", "독서")
            HashtagCategory.CREATIVE -> listOf("창의", "예술", "그림")
            HashtagCategory.DAILY -> listOf("일상", "평일", "주말")
            HashtagCategory.OTHER -> emptyList()
        }
    }

    private fun generateSearchSuggestions(
        query: String,
        searchResults: List<com.monkeys.spark.domain.model.HashtagStats>
    ): List<String> {
        return searchResults
            .filter { stats ->
                !stats.hashtag.value.equals(query, ignoreCase = true) &&
                stats.hashtag.value.contains(query.removePrefix("#"), ignoreCase = true)
            }
            .sortedByDescending { it.trendScore }
            .take(5)
            .map { it.hashtag.value }
    }
}