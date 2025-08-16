package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 레벨 정보 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class LevelInfoResponse(
    val level: Int,
    @JsonProperty("level_title")
    val levelTitle: String,
    @JsonProperty("level_title_display")
    val levelTitleDisplay: String,
    @JsonProperty("required_points")
    val requiredPoints: Int,
    @JsonProperty("next_level_points")
    val nextLevelPoints: Int?,
    val description: String,
    val benefits: List<String>,
    val icon: String,
    val color: String,
    val badge: String
)

/**
 * 사용자 레벨 진행 상황 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserLevelProgressResponse(
    @JsonProperty("current_level")
    val currentLevel: Int,
    @JsonProperty("level_title")
    val levelTitle: String,
    @JsonProperty("level_title_display")
    val levelTitleDisplay: String,
    @JsonProperty("current_points")
    val currentPoints: Int,
    @JsonProperty("total_points")
    val totalPoints: Int,
    @JsonProperty("points_to_next_level")
    val pointsToNextLevel: Int,
    @JsonProperty("level_progress_percentage")
    val levelProgressPercentage: Double,
    @JsonProperty("next_level_points")
    val nextLevelPoints: Int?,
    val icon: String,
    val color: String,
    val badge: String
)

/**
 * 전체 레벨 시스템 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class LevelSystemResponse(
    @JsonProperty("user_progress")
    val userProgress: UserLevelProgressResponse,
    @JsonProperty("all_levels")
    val allLevels: List<LevelInfoResponse>,
    @JsonProperty("level_titles")
    val levelTitles: List<LevelTitleGroupResponse>
)

/**
 * 레벨 타이틀 그룹 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class LevelTitleGroupResponse(
    val title: String,
    @JsonProperty("display_name")
    val displayName: String,
    val description: String,
    @JsonProperty("level_range")
    val levelRange: String,
    val color: String,
    val icon: String,
    val levels: List<LevelInfoResponse>
)
