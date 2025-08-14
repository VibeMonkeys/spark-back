package com.monkeys.spark.application.port.`in`.query

/**
 * 스토리 관련 Query 객체들
 */

/**
 * 스토리 피드 조회 쿼리
 */
data class StoryFeedQuery(
    val userId: String? = null,
    val sortBy: String = "latest", // "latest" or "popular"
    val page: Int = 0,
    val size: Int = 20,
    val category: String? = null
)

/**
 * 스토리 검색 쿼리
 */
data class SearchStoriesQuery(
    val keyword: String? = null,
    val hashTag: String? = null,
    val category: String? = null,
    val location: String? = null,
    val page: Int = 0,
    val size: Int = 20
)