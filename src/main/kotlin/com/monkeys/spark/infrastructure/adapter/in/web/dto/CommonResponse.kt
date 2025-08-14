package com.monkeys.spark.infrastructure.adapter.`in`.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 공통 API 응답 래퍼
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: ErrorDetail? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, data = data, message = message)
        }
        
        fun <T> success(message: String): ApiResponse<T> {
            return ApiResponse(success = true, message = message)
        }
        
        fun <T> error(message: String, errorCode: String? = null, details: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorDetail(
                    code = errorCode ?: "UNKNOWN_ERROR",
                    message = message,
                    details = details
                )
            )
        }
        
        fun <T> error(errorDetail: ErrorDetail): ApiResponse<T> {
            return ApiResponse(success = false, error = errorDetail)
        }
    }
}

/**
 * 에러 상세 정보
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDetail(
    val code: String,
    val message: String,
    val details: String? = null,
    val field: String? = null
)

/**
 * 페이징 정보
 */
data class PageInfo(
    @JsonProperty("current_page")
    val currentPage: Int,
    @JsonProperty("page_size")
    val pageSize: Int,
    @JsonProperty("total_elements")
    val totalElements: Long,
    @JsonProperty("total_pages")
    val totalPages: Int,
    @JsonProperty("has_next")
    val hasNext: Boolean,
    @JsonProperty("has_previous")
    val hasPrevious: Boolean
)

/**
 * 페이징된 응답 데이터
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PagedResponse<T>(
    val items: List<T>,
    @JsonProperty("page_info")
    val pageInfo: PageInfo
)

/**
 * 프론트엔드 요구사항에 맞춘 사용자 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    val id: String,
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
 * 스토리 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoryResponse(
    val id: String,
    val user: StoryUserResponse,
    val mission: StoryMissionResponse,
    val story: String,
    val images: List<String>,
    val location: String,
    val tags: List<String>,
    val likes: Int,
    val comments: Int,
    @JsonProperty("time_ago")
    val timeAgo: String,
    @JsonProperty("is_liked")
    val isLiked: Boolean = false
)

/**
 * 스토리 내 사용자 정보 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoryUserResponse(
    val name: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    val level: String // "레벨 8 탐험가" 형태
)

/**
 * 스토리 내 미션 정보 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoryMissionResponse(
    val title: String,
    val category: String,
    @JsonProperty("category_color")
    val categoryColor: String
)

/**
 * 리워드 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RewardResponse(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val brand: String,
    @JsonProperty("original_price")
    val originalPrice: String,
    val points: Int,
    val discount: String, // "22%" 또는 "FREE"
    @JsonProperty("image_url")
    val image: String,
    val expires: String, // "30일" 형태
    val popular: Boolean,
    @JsonProperty("is_premium")
    val isPremium: Boolean = false
)

/**
 * 사용자 리워드 응답 DTO (사용 내역용)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserRewardResponse(
    val id: String,
    val title: String,
    val brand: String,
    val points: Int,
    val code: String,
    val status: String,
    @JsonProperty("used_at")
    val usedAt: String,
    @JsonProperty("expires_at")
    val expiresAt: String? = null
)

/**
 * 홈페이지 데이터 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class HomePageResponse(
    @JsonProperty("user_summary")
    val userSummary: UserSummaryResponse,
    @JsonProperty("todays_missions")
    val todaysMissions: List<MissionResponse>,
    @JsonProperty("recent_stories")
    val recentStories: List<StoryResponse>
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
 * 미션 인증 요청 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MissionVerificationRequest(
    @JsonProperty("mission_id")
    val missionId: String,
    val story: String,
    val images: List<String> = emptyList(),
    val location: String,
    @JsonProperty("is_public")
    val isPublic: Boolean = true,
    @JsonProperty("user_tags")
    val userTags: List<String> = emptyList()
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

/**
 * 프로필 페이지 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProfilePageResponse(
    val user: UserResponse,
    val statistics: UserStatisticsResponse,
    val achievements: List<AchievementResponse>,
    @JsonProperty("recent_missions")
    val recentMissions: List<RecentMissionResponse>
)

/**
 * 업적 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AchievementResponse(
    val id: String,
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
    val id: String,
    val title: String,
    val category: String,
    @JsonProperty("completed_at")
    val completedAt: String,
    val points: Int,
    @JsonProperty("image_url")
    val image: String
)

/**
 * 리워드 페이지 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RewardsPageResponse(
    @JsonProperty("user_points")
    val userPoints: UserPointsResponse,
    @JsonProperty("available_rewards")
    val availableRewards: List<RewardResponse>,
    @JsonProperty("reward_history")
    val rewardHistory: List<UserRewardResponse>
)

/**
 * 사용자 포인트 정보 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserPointsResponse(
    val current: Int,
    val total: Int,
    @JsonProperty("this_month")
    val thisMonth: Int
)

/**
 * 스토리 댓글 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoryCommentResponse(
    val id: String,
    val userName: String,
    val userAvatarUrl: String,
    val content: String,
    val timeAgo: String
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