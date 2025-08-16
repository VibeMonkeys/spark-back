package com.monkeys.spark.application.port.`in`.command

/**
 * 스토리 좋아요 취소 커맨드
 */
data class UnlikeStoryCommand(
    val storyId: Long,
    val userId: Long
)