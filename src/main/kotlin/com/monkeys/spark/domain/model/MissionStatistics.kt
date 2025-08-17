package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Rating

/**
 * 미션 통계 Value Object
 */
data class MissionStatistics(
    var completedBy: Int = 0,
    var attemptedBy: Int = 0, // 시도한 사람 수 (시작한 사람 수)
    var averageRating: Rating = Rating(0.0),
    var totalRatings: Int = 0,
    var averageCompletionTime: Int = 0, // in minutes
    var popularityScore: Double = 0.0
) {
    fun incrementCompletedCount(): MissionStatistics {
        completedBy++
        return this
    }
    
    fun incrementAttemptedCount(): MissionStatistics {
        attemptedBy++
        return this
    }
    
    /**
     * 성공률 계산 (백분율, 반올림)
     * 시도한 사람 중에서 완료한 사람의 비율
     */
    fun getSuccessRate(): Int {
        return if (attemptedBy > 0) {
            kotlin.math.round((completedBy.toDouble() / attemptedBy.toDouble()) * 100).toInt()
        } else {
            0
        }
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