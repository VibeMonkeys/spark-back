package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.monkeys.spark.application.port.`in`.dto.UserStatsRankingItem
import com.monkeys.spark.application.port.`in`.dto.UserRankingInfo
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.stat.StatValue

/**
 * 사용자 스탯 응답 DTO
 */
data class UserStatsResponse(
    val userId: String,
    val strength: StatResponse,
    val intelligence: StatResponse,
    val creativity: StatResponse,
    val sociability: StatResponse,
    val adventurous: StatResponse,
    val discipline: StatResponse,
    val availablePoints: Int,
    val totalEarnedPoints: Int,
    val totalStats: Int,
    val averageStatValue: Double,
    val dominantStat: DominantStatResponse,
    val lastUpdatedAt: String,
    val createdAt: String
) {
    companion object {
        fun from(userStats: UserStats): UserStatsResponse {
            val (dominantStatType, dominantStatValue) = userStats.dominantStat
            
            return UserStatsResponse(
                userId = userStats.userId.value,
                strength = StatResponse.from(userStats.strength, "힘", "💪", "#EF4444"),
                intelligence = StatResponse.from(userStats.intelligence, "지능", "🧠", "#3B82F6"),
                creativity = StatResponse.from(userStats.creativity, "창의력", "🎨", "#8B5CF6"),
                sociability = StatResponse.from(userStats.sociability, "사교성", "🤝", "#10B981"),
                adventurous = StatResponse.from(userStats.adventurous, "모험심", "🗺️", "#F59E0B"),
                discipline = StatResponse.from(userStats.discipline, "규율성", "🎯", "#6B7280"),
                availablePoints = userStats.availablePoints,
                totalEarnedPoints = userStats.totalEarnedPoints,
                totalStats = userStats.totalStats,
                averageStatValue = userStats.averageStatValue,
                dominantStat = DominantStatResponse(
                    type = dominantStatType.name,
                    displayName = dominantStatType.displayName,
                    value = dominantStatValue.current,
                    icon = dominantStatType.icon,
                    color = dominantStatType.color
                ),
                lastUpdatedAt = userStats.lastUpdatedAt.toString(),
                createdAt = userStats.createdAt.toString()
            )
        }
    }
}

/**
 * 개별 스탯 응답 DTO
 */
data class StatResponse(
    val current: Int,
    val allocated: Int,
    val base: Int,
    val grade: StatGradeResponse,
    val displayName: String,
    val icon: String,
    val color: String
) {
    companion object {
        fun from(statValue: StatValue, displayName: String, icon: String, color: String): StatResponse {
            return StatResponse(
                current = statValue.current,
                allocated = statValue.allocated,
                base = statValue.base,
                grade = StatGradeResponse.from(statValue.grade),
                displayName = displayName,
                icon = icon,
                color = color
            )
        }
    }
}

/**
 * 스탯 등급 응답 DTO
 */
data class StatGradeResponse(
    val name: String,
    val displayName: String,
    val minValue: Int,
    val maxValue: Int,
    val color: String
) {
    companion object {
        fun from(grade: com.monkeys.spark.domain.vo.stat.StatGrade): StatGradeResponse {
            return StatGradeResponse(
                name = grade.name,
                displayName = grade.displayName,
                minValue = grade.minValue,
                maxValue = grade.maxValue,
                color = grade.color
            )
        }
    }
}

/**
 * 주력 스탯 응답 DTO
 */
data class DominantStatResponse(
    val type: String,
    val displayName: String,
    val value: Int,
    val icon: String,
    val color: String
)

/**
 * 스탯 랭킹 응답 DTO
 */
data class UserStatsRankingResponse(
    val rank: Int,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val statValue: Int,
    val statType: String?,
    val totalStats: Int
) {
    companion object {
        fun from(item: UserStatsRankingItem): UserStatsRankingResponse {
            return UserStatsRankingResponse(
                rank = item.rank,
                userId = item.userId,
                username = item.username,
                avatarUrl = item.avatarUrl,
                statValue = item.statValue,
                statType = item.statType?.name,
                totalStats = item.totalStats
            )
        }
    }
}

/**
 * 사용자 랭킹 정보 응답 DTO
 */
data class UserRankingInfoResponse(
    val userId: String,
    val totalStatsRank: Int,
    val strengthRank: Int,
    val intelligenceRank: Int,
    val creativityRank: Int,
    val sociabilityRank: Int,
    val adventurousRank: Int,
    val disciplineRank: Int,
    val totalUsers: Long
) {
    companion object {
        fun from(info: UserRankingInfo): UserRankingInfoResponse {
            return UserRankingInfoResponse(
                userId = info.userId,
                totalStatsRank = info.totalStatsRank,
                strengthRank = info.strengthRank,
                intelligenceRank = info.intelligenceRank,
                creativityRank = info.creativityRank,
                sociabilityRank = info.sociabilityRank,
                adventurousRank = info.adventurousRank,
                disciplineRank = info.disciplineRank,
                totalUsers = info.totalUsers
            )
        }
    }
}