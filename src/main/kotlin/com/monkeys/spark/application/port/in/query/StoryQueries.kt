package com.monkeys.spark.application.port.`in`.query

/**
 * 스토리 관련 Query 객체들
 */

/**
 * 스토리 검색 쿼리 (커서 기반으로 변경)
 */
data class SearchStoriesQuery(
    val keyword: String? = null,
    val hashTag: String? = null,
    val category: String? = null,
    val location: String? = null,
    val cursor: Long? = null,
    val size: Int = 20,
    val isNext: Boolean = true
)