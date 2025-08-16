package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.Rating
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.stat.CategoryStat

/**
 * 사용자 통계 Value Object
 */
data class UserStatistics(
    var categoryStats: MutableMap<MissionCategory, CategoryStat> = mutableMapOf(),
    var thisMonthPoints: Points = Points(0),
    var thisMonthMissions: Int = 0,
    var averageRating: Rating = Rating(0.0),
    var totalRatings: Int = 0
) {
    init {
        // Initialize all categories with zero stats
        MissionCategory.values().forEach { category ->
            categoryStats.putIfAbsent(category, CategoryStat())
        }
    }

    fun incrementCategoryCount(category: MissionCategory): UserStatistics {
        val stat = categoryStats[category] ?: CategoryStat()
        categoryStats[category] = stat.copy(completed = stat.completed + 1)
        return this
    }
}