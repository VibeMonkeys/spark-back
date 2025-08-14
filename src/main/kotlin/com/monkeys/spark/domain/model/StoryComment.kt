package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import java.time.LocalDateTime

/**
 * Story Comment Domain Object
 */
data class StoryComment(
    val id: String,
    val storyId: StoryId,
    val userId: UserId,
    val userName: UserName,
    val userAvatarUrl: AvatarUrl,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            storyId: StoryId,
            userId: UserId,
            userName: UserName,
            userAvatarUrl: AvatarUrl,
            content: String
        ): StoryComment {
            require(content.isNotBlank()) { "Comment content cannot be blank" }
            require(content.length <= 500) { "Comment cannot exceed 500 characters" }
            
            return StoryComment(
                id = java.util.UUID.randomUUID().toString(),
                storyId = storyId,
                userId = userId,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                content = content
            )
        }
    }
    
    fun getTimeAgo(): String {
        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(createdAt, now)
        
        return when {
            duration.toDays() > 0 -> "${duration.toDays()}일 전"
            duration.toHours() > 0 -> "${duration.toHours()}시간 전"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}분 전"
            else -> "방금 전"
        }
    }
}