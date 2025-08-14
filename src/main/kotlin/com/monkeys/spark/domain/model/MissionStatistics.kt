package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Rating

/**
 * 미션 통계 Value Object
 */
data class MissionStatistics(
    var completedBy: Int = 0,
    var averageRating: Rating = Rating(0.0),
    var totalRatings: Int = 0,
    var averageCompletionTime: Int = 0, // in minutes
    var popularityScore: Double = 0.0
) {
    fun incrementCompletedCount(): MissionStatistics {
        completedBy++
        return this
    }
    
    fun addRating(newRating: Rating): MissionStatistics {
        val totalScore = averageRating.value * totalRatings + newRating.value
        totalRatings++
        averageRating = Rating(totalScore / totalRatings)
        return this
    }
    
    fun updateCompletionTime(completionMinutes: Int): MissionStatistics {
        // Simple moving average - could be improved with weighted average
        averageCompletionTime = if (completedBy == 1) {
            completionMinutes
        } else {
            (averageCompletionTime * (completedBy - 1) + completionMinutes) / completedBy
        }
        return this
    }
}