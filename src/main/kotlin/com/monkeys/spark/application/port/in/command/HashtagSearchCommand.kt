package com.monkeys.spark.application.port.`in`.command

import com.monkeys.spark.domain.vo.hashtag.HashtagCategory
import com.monkeys.spark.domain.vo.hashtag.HashtagSortCriteria
import java.time.LocalDate

/**
 * 해시태그 검색 커맨드
 */
data class HashtagSearchCommand(
    val query: String,
    val sortBy: HashtagSortCriteria = HashtagSortCriteria.RELEVANCE,
    val category: HashtagCategory? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val includeStoryCount: Boolean = false,
    val userId: String? = null // 개인화된 검색을 위한 사용자 ID
) {
    init {
        require(query.isNotBlank()) { "검색어는 비어있을 수 없습니다" }
        require(limit > 0 && limit <= 100) { "limit은 1-100 사이의 값이어야 합니다" }
        require(offset >= 0) { "offset은 0 이상이어야 합니다" }
        dateFrom?.let { from ->
            dateTo?.let { to ->
                require(!from.isAfter(to)) { "시작 날짜는 종료 날짜보다 이전이어야 합니다" }
            }
        }
    }
    
    /**
     * 검색어 정규화
     */
    fun normalizedQuery(): String {
        return if (query.startsWith("#")) query else "#$query"
    }
    
    /**
     * 날짜 필터 적용 여부
     */
    fun hasDateFilter(): Boolean {
        return dateFrom != null && dateTo != null
    }
    
    /**
     * 카테고리 필터 적용 여부
     */
    fun hasCategoryFilter(): Boolean {
        return category != null
    }
}