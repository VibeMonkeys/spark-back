package com.monkeys.spark.application.port.`in`.command

/**
 * 스토리 좋아요 커맨드
 */
data class LikeStoryCommand(
    val storyId: Long,
    val userId: Long
)