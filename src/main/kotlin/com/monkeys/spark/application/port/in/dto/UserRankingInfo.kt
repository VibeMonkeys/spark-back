package com.monkeys.spark.application.port.`in`.dto

/**
 * 사용자 랭킹 정보
 */
data class UserRankingInfo(
    val userId: Long,
    val totalStatsRank: Int,
    val strengthRank: Int,
    val intelligenceRank: Int,
    val creativityRank: Int,
    val sociabilityRank: Int,
    val adventurousRank: Int,
    val disciplineRank: Int,
    val totalUsers: Long
)