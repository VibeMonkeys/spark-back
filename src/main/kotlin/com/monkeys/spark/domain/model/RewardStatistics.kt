package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import java.time.LocalDateTime

/**
 * Reward Statistics - for admin/analytics
 */
data class RewardStatistics(
    val rewardId: RewardId,
    val totalExchanges: Int,
    val totalPointsSpent: Points,
    val averageExchangesPerDay: Double,
    val popularityRank: Int,
    val conversionRate: Double, // percentage of users who saw it vs exchanged it
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)