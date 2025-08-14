package com.monkeys.spark.application.port.`in`.command

/**
 * 스토리 관련 Command 객체들
 */

/**
 * 스토리 생성 커맨드
 */
data class CreateStoryCommand(
    val userId: String,
    val missionId: String,
    val storyText: String,
    val images: List<String> = emptyList(),
    val location: String,
    val isPublic: Boolean = true,
    val userTags: List<String> = emptyList()
)


/**
 * 스토리 좋아요 커맨드
 */
data class LikeStoryCommand(
    val storyId: String,
    val userId: String
)

/**
 * 스토리 좋아요 취소 커맨드
 */
data class UnlikeStoryCommand(
    val storyId: String,
    val userId: String
)

/**
 * 댓글 추가 커맨드
 */
data class AddCommentCommand(
    val storyId: String,
    val userId: String,
    val content: String
)

/**
 * 스토리 업데이트 커맨드
 */
data class UpdateStoryCommand(
    val storyId: String,
    val userId: String,
    val storyText: String,
    val userTags: List<String> = emptyList(),
    val isPublic: Boolean = true
)

/**
 * 스토리 삭제 커맨드
 */
data class DeleteStoryCommand(
    val storyId: String,
    val userId: String
)

