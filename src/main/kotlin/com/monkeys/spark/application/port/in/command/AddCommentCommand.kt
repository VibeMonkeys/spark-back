package com.monkeys.spark.application.port.`in`.command

/**
 * 댓글 추가 커맨드
 */
data class AddCommentCommand(
    val storyId: String,
    val userId: String,
    val content: String
)