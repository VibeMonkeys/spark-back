package com.monkeys.spark.application.port.`in`.dto

import com.monkeys.spark.domain.vo.stat.StatType

/**
 * 스탯 랭킹 아이템
 */
data class UserStatsRankingItem(
    val rank: Int,
    val userId: Long,
    val username: String,
    val avatarUrl: String?,
    val statValue: Int,
    val statType: StatType? = null,
    val totalStats: Int
)