package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.*
import com.monkeys.spark.domain.vo.story.*
import java.time.LocalDateTime

// Story Domain Aggregate Root
data class Story(
    var id: StoryId,
    var userId: UserId,
    var missionId: MissionId,
    var missionTitle: MissionTitle,
    var missionCategory: MissionCategory,
    var storyText: StoryText,
    var images: MutableList<ImageUrl> = mutableListOf(),
    var location: Location,
    var autoTags: MutableList<HashTag> = mutableListOf(),
    var userTags: MutableList<HashTag> = mutableListOf(),
    var isPublic: Boolean = true,
    var likes: LikeCount = LikeCount(0),
    var comments: CommentCount = CommentCount(0),
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            userId: UserId,
            missionId: MissionId,
            missionTitle: MissionTitle,
            missionCategory: MissionCategory,
            storyText: String,
            images: List<String> = emptyList(),
            location: String,
            isPublic: Boolean = true
        ): Story {
            val story = Story(
                id = StoryId.generate(),
                userId = userId,
                missionId = missionId,
                missionTitle = missionTitle,
                missionCategory = missionCategory,
                storyText = StoryText(storyText),
                images = images.map { ImageUrl(it) }.toMutableList(),
                location = Location(location),
                isPublic = isPublic
            )
            
            // Generate auto tags based on mission category and content
            story.generateAutoTags()
            
            return story
        }
    }
    
    fun addImage(imageUrl: String): Story {
        require(images.size < 3) { "Maximum 3 images allowed per story" }
        images.add(ImageUrl(imageUrl))
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun removeImage(imageUrl: String): Story {
        images.removeIf { it.value == imageUrl }
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun updateStoryText(newText: String): Story {
        storyText = StoryText(newText)
        updatedAt = LocalDateTime.now()
        // Regenerate auto tags based on new content
        generateAutoTags()
        return this
    }
    
    fun addUserTag(tag: String): Story {
        val hashTag = if (tag.startsWith("#")) HashTag(tag) else HashTag("#$tag")
        if (!userTags.contains(hashTag)) {
            userTags.add(hashTag)
            updatedAt = LocalDateTime.now()
        }
        return this
    }
    
    fun removeUserTag(tag: String): Story {
        val hashTag = if (tag.startsWith("#")) HashTag(tag) else HashTag("#$tag")
        userTags.remove(hashTag)
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun like(): Story {
        likes = likes.increment()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun unlike(): Story {
        likes = likes.decrement()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun addComment(): Story {
        comments = comments.increment()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun setVisibility(public: Boolean): Story {
        isPublic = public
        updatedAt = LocalDateTime.now()
        return this
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
    
    private fun generateAutoTags() {
        autoTags.clear()
        
        // Add category-based tag
        when (missionCategory) {
            MissionCategory.SOCIAL -> autoTags.add(HashTag("#사교적"))
            MissionCategory.ADVENTURE -> autoTags.add(HashTag("#모험"))
            MissionCategory.HEALTH -> autoTags.add(HashTag("#건강"))
            MissionCategory.CREATIVE -> autoTags.add(HashTag("#창의적"))
            MissionCategory.LEARNING -> autoTags.add(HashTag("#학습"))
        }
        
        // Add time-based tag
        val hour = createdAt.hour
        when (hour) {
            in 6..11 -> autoTags.add(HashTag("#아침시간"))
            in 12..17 -> autoTags.add(HashTag("#오후시간"))
            in 18..23 -> autoTags.add(HashTag("#저녁시간"))
            else -> autoTags.add(HashTag("#새벽시간"))
        }
        
        // Add day-based tag
        val dayOfWeek = createdAt.dayOfWeek
        when (dayOfWeek.value) {
            in 1..5 -> autoTags.add(HashTag("#평일"))
            else -> autoTags.add(HashTag("#주말"))
        }
        
        // Add location-based tag if available
        if (location.value.isNotBlank()) {
            autoTags.add(HashTag("#${location.value}"))
        }
        
        // Content-based tags (simple keyword matching)
        val contentLower = storyText.value.lowercase()
        when {
            contentLower.contains("카페") || contentLower.contains("커피") -> autoTags.add(HashTag("#카페"))
            contentLower.contains("음식") || contentLower.contains("맛집") -> autoTags.add(HashTag("#음식"))
            contentLower.contains("운동") || contentLower.contains("걷기") -> autoTags.add(HashTag("#운동"))
            contentLower.contains("친구") || contentLower.contains("사람") -> autoTags.add(HashTag("#소셜"))
            contentLower.contains("새로운") || contentLower.contains("처음") -> autoTags.add(HashTag("#새로운경험"))
        }
    }
    
    fun getAllTags(): List<HashTag> {
        return (autoTags + userTags).distinct()
    }
}