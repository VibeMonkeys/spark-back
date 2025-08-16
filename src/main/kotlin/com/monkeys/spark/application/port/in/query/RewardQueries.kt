package com.monkeys.spark.application.port.`in`.query

/**
 * 리워드 관련 Query 객체들
 */

/**
 * 사용 가능한 리워드 조회 쿼리
 */
data class AvailableRewardsQuery(
    val userId: Long,
    val category: String? = null,
    val maxPoints: Int? = null,
    val page: Int = 0,
    val size: Int = 20
)

/**
 * 사용자 리워드 조회 쿼리
 */
data class UserRewardsQuery(
    val userId: Long,
    val status: String? = null, // "AVAILABLE", "USED", "EXPIRED"
    val page: Int = 0,
    val size: Int = 20
)

/**
 * 사용자 포인트 요약
 */
data class UserPointsSummary(
    val current: Int,
    val total: Int,
    val thisMonth: Int,
    val spent: Int,
    val thisMonthSpent: Int
)

/**
 * 리워드 통계
 */
data class RewardStatistics(
    val totalExchanged: Int,
    val totalPointsSpent: Int,
    val thisMonthExchanged: Int,
    val thisMonthPointsSpent: Int,
    val favoriteCategory: String,
    val mostUsedBrand: String
)