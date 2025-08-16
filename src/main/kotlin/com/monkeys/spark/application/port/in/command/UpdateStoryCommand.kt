package com.monkeys.spark.application.port.`in`.command

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