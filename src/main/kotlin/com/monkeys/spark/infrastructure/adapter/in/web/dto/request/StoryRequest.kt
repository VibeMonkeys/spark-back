package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 댓글 추가 요청 DTO
 */
data class AddCommentRequest(
    val content: String
)

/**
 * 스토리 수정 요청 DTO
 */
data class UpdateStoryRequest(
    val storyText: String,
    val userTags: List<String> = emptyList(),
    val isPublic: Boolean = true
)

/**
 * 미션 인증 요청 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MissionVerificationRequest(
    @JsonProperty("mission_id")
    val missionId: Long,
    val story: String,
    val images: List<String> = emptyList(),
    val location: String,
    @JsonProperty("is_public")
    val isPublic: Boolean = true,
    @JsonProperty("user_tags")
    val userTags: List<String> = emptyList()
)