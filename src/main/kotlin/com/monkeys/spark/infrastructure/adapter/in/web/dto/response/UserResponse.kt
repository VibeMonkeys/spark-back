package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 프론트엔드 요구사항에 맞춘 사용자 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    val level: Int,
    @JsonProperty("level_title")
    val levelTitle: String,
    @JsonProperty("current_points")
    val currentPoints: Int,
    @JsonProperty("total_points")
    val totalPoints: Int,
    @JsonProperty("current_streak")
    val currentStreak: Int,
    @JsonProperty("longest_streak")
    val longestStreak: Int,
    @JsonProperty("completed_missions")
    val completedMissions: Int,
    @JsonProperty("total_days")
    val totalDays: Int,
    @JsonProperty("join_date")
    val joinDate: String,
    val bio: String? = null,
    val preferences: Map<String, Boolean>? = null,
    val statistics: UserStatisticsResponse? = null
)

/**
 * 사용자 통계 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserStatisticsResponse(
    @JsonProperty("category_stats")
    val categoryStats: List<CategoryStatResponse>,
    @JsonProperty("this_month_points")
    val thisMonthPoints: Int,
    @JsonProperty("this_month_missions")
    val thisMonthMissions: Int,
    @JsonProperty("average_rating")
    val averageRating: Double
)

/**
 * 카테고리별 통계 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CategoryStatResponse(
    val name: String,
    val completed: Int,
    val total: Int,
    val percentage: Double,
    val color: String
)

/**
 * 리더보드 사용자 응답 DTO
 */
data class LeaderboardUserResponse(
    val rank: Int,
    val userId: Long,
    val name: String,
    val avatarUrl: String,
    val level: Int,
    val levelTitle: String,
    val points: Int,
    val streak: Int
)

/**
 * 사용자 요약 정보 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserSummaryResponse(
    val name: String,
    val level: Int,
    @JsonProperty("level_title")
    val levelTitle: String,
    @JsonProperty("current_points")
    val currentPoints: Int,
    @JsonProperty("current_streak")
    val currentStreak: Int,
    @JsonProperty("progress_to_next_level")
    val progressToNextLevel: Int, // 0-100 percentage
    @JsonProperty("points_to_next_level")
    val pointsToNextLevel: Int
)


/**
 * 프로필 페이지 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProfilePageResponse(
    val user: UserResponse,
    val statistics: UserStatisticsResponse,
    val achievements: List<ProfileAchievementResponse>,
    @JsonProperty("recent_missions")
    val recentMissions: List<RecentMissionResponse>
)

/**
 * 프로필용 업적 요약 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProfileAchievementResponse(
    val id: Long,
    val name: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean
)

/**
 * 최근 미션 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RecentMissionResponse(
    val id: Long,
    val title: String,
    val category: String,
    @JsonProperty("completed_at")
    val completedAt: String,
    val points: Int,
    @JsonProperty("image_url")
    val image: String
)