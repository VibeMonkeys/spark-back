package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 미션 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MissionResponse(
    val id: String,
    val title: String,
    val description: String,
    @JsonProperty("detailed_description")
    val detailedDescription: String? = null,
    val category: String,
    @JsonProperty("category_color")
    val categoryColor: String,
    val difficulty: String,
    val status: String,
    @JsonProperty("reward_points")
    val points: Int,
    val duration: String, // "20분" 형태
    @JsonProperty("image_url")
    val image: String,
    val tips: List<String>? = null,
    val progress: Int? = null,
    @JsonProperty("time_left")
    val timeLeft: String? = null,
    @JsonProperty("completed_by")
    val completedBy: Int? = null,
    @JsonProperty("average_rating")
    val averageRating: Double? = null,
    @JsonProperty("assigned_at")
    val assignedAt: LocalDateTime? = null,
    @JsonProperty("expires_at")
    val expiresAt: LocalDateTime? = null
)

/**
 * 미션 상세 응답 DTO (MissionDetail 컴포넌트용)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MissionDetailResponse(
    val id: String,
    val title: String,
    val description: String,
    @JsonProperty("detailed_description")
    val detailedDescription: String,
    val category: String,
    @JsonProperty("category_color")
    val categoryColor: String,
    val difficulty: String,
    @JsonProperty("reward_points")
    val points: Int,
    val duration: String,
    @JsonProperty("image_url")
    val image: String,
    val tips: List<String>,
    @JsonProperty("completed_by")
    val completedBy: Int,
    @JsonProperty("average_rating")
    val averageRating: Double,
    @JsonProperty("similar_missions")
    val similarMissions: List<SimilarMissionResponse>
)

/**
 * 유사 미션 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimilarMissionResponse(
    val id: String,
    val title: String,
    val difficulty: String,
    val points: Int
)

/**
 * 미션 완료 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MissionCompletionResponse(
    val mission: MissionResponse,
    @JsonProperty("points_earned")
    val pointsEarned: Int,
    @JsonProperty("streak_count")
    val streakCount: Int,
    @JsonProperty("level_up")
    val levelUp: Boolean = false,
    @JsonProperty("new_level")
    val newLevel: Int? = null,
    @JsonProperty("total_points")
    val totalPoints: Int,
    @JsonProperty("this_month_points")
    val thisMonthPoints: Int
)

/**
 * 미션 인증 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MissionVerificationResponse(
    @JsonProperty("story_id")
    val storyId: String,
    @JsonProperty("points_earned")
    val pointsEarned: Int,
    @JsonProperty("streak_count")
    val streakCount: Int,
    @JsonProperty("level_up")
    val levelUp: Boolean = false,
    @JsonProperty("new_level")
    val newLevel: Int? = null
)