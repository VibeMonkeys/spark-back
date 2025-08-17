package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.command.HashtagSearchCommand
import com.monkeys.spark.application.port.`in`.dto.HashtagSearchResult
import com.monkeys.spark.application.port.`in`.dto.HashtagStatsDto
import com.monkeys.spark.application.port.`in`.dto.HashtagTrendDto
import com.monkeys.spark.application.port.`in`.dto.HashtagStatsSummaryDto
import com.monkeys.spark.domain.service.HashtagCategory
import com.monkeys.spark.domain.service.HashtagSortCriteria
import java.time.LocalDate

/**
 * 해시태그 검색 유스케이스 인터페이스
 */
interface HashtagSearchUseCase {
    
    /**
     * 해시태그 검색
     */
    fun searchHashtags(command: HashtagSearchCommand): HashtagSearchResult
    
    /**
     * 해시태그 자동완성
     */
    fun autocompleteHashtags(prefix: String, limit: Int = 10): List<String>
    
    /**
     * 인기 해시태그 조회
     */
    fun getPopularHashtags(date: LocalDate = LocalDate.now(), limit: Int = 20): List<HashtagStatsDto>
    
    /**
     * 트렌딩 해시태그 조회
     */
    fun getTrendingHashtags(date: LocalDate = LocalDate.now(), limit: Int = 20): List<HashtagStatsDto>
    
    /**
     * 카테고리별 해시태그 조회
     */
    fun getHashtagsByCategory(
        category: HashtagCategory,
        date: LocalDate = LocalDate.now(),
        limit: Int = 20
    ): List<HashtagStatsDto>
    
    /**
     * 해시태그 상세 정보 조회
     */
    fun getHashtagDetails(hashtag: String): HashtagStatsDto?
    
    /**
     * 해시태그 트렌드 분석
     */
    fun getHashtagTrends(
        hashtag: String,
        days: Int = 7
    ): List<HashtagTrendDto>
    
    /**
     * 해시태그 추천
     */
    fun getRecommendedHashtags(
        userId: String?,
        preferences: Set<HashtagCategory> = emptySet(),
        limit: Int = 10
    ): List<HashtagStatsDto>
    
    /**
     * 해시태그 사용량 통계 요약
     */
    fun getHashtagStatsSummary(date: LocalDate = LocalDate.now()): HashtagStatsSummaryDto
}

