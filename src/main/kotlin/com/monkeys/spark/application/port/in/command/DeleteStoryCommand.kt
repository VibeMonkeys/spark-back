package com.monkeys.spark.application.port.`in`.command

/**
 * 스토리 삭제 커맨드
 */
data class DeleteStoryCommand(
    val storyId: String,
    val userId: String
)