package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.mission.MissionTitle
import com.monkeys.spark.domain.vo.story.*
import java.time.LocalDateTime

// Story Domain Aggregate Root
data class Story(
    var id: StoryId,
    var userId: UserId,
    var storyType: StoryType,
    var missionId: MissionId?,
    var missionTitle: MissionTitle?,
    var missionCategory: MissionCategory?,
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
        // 미션 인증 스토리 생성
        fun createMissionProof(
            userId: UserId,
            missionId: MissionId,
            missionTitle: MissionTitle,
            missionCategory: MissionCategory,
            storyText: String,
            images: List<String> = emptyList(),
            location: String,
            userTags: List<String> = emptyList(),
            isPublic: Boolean = true
        ): Story {
            val story = Story(
                id = StoryId.generate(),
                userId = userId,
                storyType = StoryType.MISSION_PROOF,
                missionId = missionId,
                missionTitle = missionTitle,
                missionCategory = missionCategory,
                storyText = StoryText(storyText),
                images = images.map { ImageUrl(it) }.toMutableList(),
                location = Location(location),
                userTags = userTags.map { HashTag(it) }.toMutableList(),
                isPublic = isPublic
            )

            // Generate auto tags based on mission category and content
            story.generateAutoTags()

            return story
        }
        
        // 자유 스토리 생성
        fun createFreeStory(
            userId: UserId,
            storyText: String,
            images: List<String> = emptyList(),
            location: String,
            userTags: List<String> = emptyList(),
            isPublic: Boolean = true
        ): Story {
            val story = Story(
                id = StoryId.generate(),
                userId = userId,
                storyType = StoryType.FREE_STORY,
                missionId = null,
                missionTitle = null,
                missionCategory = null,
                storyText = StoryText(storyText),
                images = images.map { ImageUrl(it) }.toMutableList(),
                location = Location(location),
                userTags = userTags.map { HashTag(it) }.toMutableList(),
                isPublic = isPublic
            )

            // Generate auto tags for free story
            story.generateAutoTags()

            return story
        }
        
        // 기존 호환성을 위한 create 메서드 (미션 인증으로 처리)
        @Deprecated("Use createMissionProof instead", ReplaceWith("createMissionProof(userId, missionId, missionTitle, missionCategory, storyText, images, location, userTags, isPublic)"))
        fun create(
            userId: UserId,
            missionId: MissionId,
            missionTitle: MissionTitle,
            missionCategory: MissionCategory,
            storyText: String,
            images: List<String> = emptyList(),
            location: String,
            userTags: List<String> = emptyList(),
            isPublic: Boolean = true
        ): Story = createMissionProof(userId, missionId, missionTitle, missionCategory, storyText, images, location, userTags, isPublic)
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

        // Add story type tag
        when (storyType) {
            StoryType.MISSION_PROOF -> autoTags.add(HashTag("#미션인증"))
            StoryType.FREE_STORY -> autoTags.add(HashTag("#일상"))
        }

        // Add category-based tag (only for mission proof)
        missionCategory?.let { category ->
            if (storyType == StoryType.MISSION_PROOF) {
                when (category) {
                    MissionCategory.SOCIAL -> autoTags.add(HashTag("#사교적"))
                    MissionCategory.ADVENTURE -> autoTags.add(HashTag("#모험"))
                    MissionCategory.HEALTH -> autoTags.add(HashTag("#건강"))
                    MissionCategory.CREATIVE -> autoTags.add(HashTag("#창의적"))
                    MissionCategory.LEARNING -> autoTags.add(HashTag("#학습"))
                }
            }
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
            contentLower.contains("행복") || contentLower.contains("기쁘") -> autoTags.add(HashTag("#행복"))
            contentLower.contains("여행") || contentLower.contains("여행지") -> autoTags.add(HashTag("#여행"))
            contentLower.contains("독서") || contentLower.contains("책") -> autoTags.add(HashTag("#독서"))
            contentLower.contains("영화") || contentLower.contains("드라마") -> autoTags.add(HashTag("#문화생활"))
        }
    }

    fun getAllTags(): List<HashTag> {
        return (autoTags + userTags).distinct()
    }
}