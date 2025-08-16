package com.monkeys.spark.application.port.`in`.command

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