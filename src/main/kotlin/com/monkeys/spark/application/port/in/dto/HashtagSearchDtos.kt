package com.monkeys.spark.application.port.`in`.dto

import com.monkeys.spark.domain.vo.hashtag.HashtagCategory
import com.monkeys.spark.domain.vo.hashtag.HashtagLifecycle
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 해시태그 검색 결과 DTO
 */
data class HashtagSearchResult(
    val hashtags: List<HashtagStatsDto>,
    val totalCount: Long,
    val hasMore: Boolean,
    val searchQuery: String,
    val suggestions: List<String> = emptyList() // 검색어 제안
)

/**
 * 해시태그 통계 DTO
 */
data class HashtagStatsDto(
    val id: String,
    val hashtag: String,
    val category: String,
    val categoryDisplayName: String,
    val categoryColor: String,
    val dailyCount: Int,
    val weeklyCount: Int,
    val monthlyCount: Int,
    val totalCount: Int,
    val trendScore: Double,
    val lifecycle: String,
    val lifecycleDisplayName: String,
    val lastUsedAt: LocalDateTime,
    val date: LocalDate,
    val isPopular: Boolean,
    val isTrending: Boolean,
    val relatedStoryCount: Int? = null, // 관련 스토리 수 (옵션)
    val growthRate: Double? = null, // 성장률 (옵션)
    val recommendationScore: Double? = null // 추천 점수 (옵션)
)

/**
 * 해시태그 트렌드 DTO
 */
data class HashtagTrendDto(
    val date: LocalDate,
    val dailyCount: Int,
    val trendScore: Double,
    val growthRate: Double
)

/**
 * 해시태그 자동완성 DTO
 */
data class HashtagAutocompleteDto(
    val hashtag: String,
    val displayText: String,
    val category: String,
    val usageCount: Int,
    val isPopular: Boolean
)

/**
 * 해시태그 통계 요약 DTO
 */
data class HashtagStatsSummaryDto(
    val totalHashtags: Long,
    val totalDailyUsage: Long,
    val averageTrendScore: Double,
    val topCategories: List<CategoryUsageDto>,
    val newHashtags: Int,
    val trendingCount: Int,
    val popularCount: Int
)

/**
 * 카테고리별 사용량 DTO
 */
data class CategoryUsageDto(
    val category: String,
    val displayName: String,
    val count: Int,
    val percentage: Double,
    val color: String
)