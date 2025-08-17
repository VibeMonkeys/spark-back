package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 스토리 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoryResponse(
    val id: Long,
    val user: StoryUserResponse,
    val mission: StoryMissionResponse?,
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
 * 스토리 댓글 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoryCommentResponse(
    val id: Long,
    val userName: String,
    val userAvatarUrl: String,
    val content: String,
    val timeAgo: String
)