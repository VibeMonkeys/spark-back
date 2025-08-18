package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.HashtagSearchUseCase
import com.monkeys.spark.application.port.`in`.command.HashtagSearchCommand
import com.monkeys.spark.domain.vo.hashtag.HashtagCategory
import com.monkeys.spark.domain.vo.hashtag.HashtagSortCriteria
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * 해시태그 검색 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/hashtags")
class HashtagController(
    private val hashtagSearchUseCase: HashtagSearchUseCase
) {

    @GetMapping("/search")
    fun searchHashtags(
        @RequestParam query: String,
        @RequestParam(defaultValue = "RELEVANCE") sortBy: HashtagSortCriteria,
        @RequestParam(required = false) category: HashtagCategory?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateFrom: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dateTo: LocalDate?,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "false") includeStoryCount: Boolean,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ApiResponse<*> {
        val command = HashtagSearchCommand(
            query = query,
            sortBy = sortBy,
            category = category,
            dateFrom = dateFrom,
            dateTo = dateTo,
            limit = limit,
            offset = offset,
            includeStoryCount = includeStoryCount,
            userId = userDetails?.username
        )
        
        val result = hashtagSearchUseCase.searchHashtags(command)
        return ApiResponse.success(result)
    }

    @GetMapping("/autocomplete")
    fun autocompleteHashtags(
        @RequestParam prefix: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ApiResponse<*> {
        val suggestions = hashtagSearchUseCase.autocompleteHashtags(prefix, limit)
        return ApiResponse.success(suggestions)
    }

    @GetMapping("/popular")
    fun getPopularHashtags(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @RequestParam(defaultValue = "20") limit: Int
    ): ApiResponse<*> {
        val result = hashtagSearchUseCase.getPopularHashtags(date ?: LocalDate.now(), limit)
        return ApiResponse.success(result)
    }

    @GetMapping("/trending")
    fun getTrendingHashtags(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @RequestParam(defaultValue = "20") limit: Int
    ): ApiResponse<*> {
        val result = hashtagSearchUseCase.getTrendingHashtags(date ?: LocalDate.now(), limit)
        return ApiResponse.success(result)
    }

    @GetMapping("/category/{category}")
    fun getHashtagsByCategory(
        @PathVariable category: HashtagCategory,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @RequestParam(defaultValue = "20") limit: Int
    ): ApiResponse<*> {
        val result = hashtagSearchUseCase.getHashtagsByCategory(category, date ?: LocalDate.now(), limit)
        return ApiResponse.success(result)
    }

    @GetMapping("/{hashtag}")
    fun getHashtagDetails(
        @PathVariable hashtag: String
    ): ApiResponse<*> {
        val result = hashtagSearchUseCase.getHashtagDetails(hashtag)
        return if (result != null) {
            ApiResponse.success(result)
        } else {
            ApiResponse.error<Any>("해시태그를 찾을 수 없습니다", "HASHTAG_NOT_FOUND")
        }
    }

    @GetMapping("/{hashtag}/trends")
    fun getHashtagTrends(
        @PathVariable hashtag: String,
        @RequestParam(defaultValue = "7") days: Int
    ): ApiResponse<*> {
        val result = hashtagSearchUseCase.getHashtagTrends(hashtag, days)
        return ApiResponse.success(result)
    }

    @GetMapping("/recommendations")
    fun getRecommendedHashtags(
        @RequestParam(required = false) categories: String?,
        @RequestParam(defaultValue = "10") limit: Int,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ApiResponse<*> {
        val preferences = HashtagCategory.parseMultiple(categories)
        
        val result = hashtagSearchUseCase.getRecommendedHashtags(
            userDetails?.username,
            preferences,
            limit
        )
        return ApiResponse.success(result)
    }

    @GetMapping("/stats/summary")
    fun getHashtagStatsSummary(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ApiResponse<*> {
        val result = hashtagSearchUseCase.getHashtagStatsSummary(date ?: LocalDate.now())
        return ApiResponse.success(result)
    }

    @GetMapping("/categories")
    fun getHashtagCategories(): ApiResponse<*> {
        val categories = HashtagCategory.values().map { category ->
            mapOf(
                "name" to category.name,
                "displayName" to category.displayName,
                "color" to category.color
            )
        }
        return ApiResponse.success(categories)
    }

    @GetMapping("/sort-criteria")
    fun getSortCriteria(): ApiResponse<*> {
        val sortCriteria = HashtagSortCriteria.values().map { criteria ->
            mapOf(
                "name" to criteria.name,
                "displayName" to when (criteria) {
                    HashtagSortCriteria.RELEVANCE -> "관련성"
                    HashtagSortCriteria.POPULARITY -> "인기도"
                    HashtagSortCriteria.RECENT -> "최근 사용"
                    HashtagSortCriteria.ALPHABETICAL -> "알파벳순"
                    HashtagSortCriteria.USAGE_COUNT -> "사용 횟수"
                }
            )
        }
        return ApiResponse.success(sortCriteria)
    }
}