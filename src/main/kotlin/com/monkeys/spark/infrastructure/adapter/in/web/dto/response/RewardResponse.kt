package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

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