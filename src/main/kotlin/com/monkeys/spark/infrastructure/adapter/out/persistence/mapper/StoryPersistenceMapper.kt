package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.mission.MissionTitle
import com.monkeys.spark.domain.vo.story.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryEntity
import org.springframework.stereotype.Component

@Component
class StoryPersistenceMapper {
    
    fun toEntity(domain: Story): StoryEntity {
        val entity = StoryEntity()
        entity.id = domain.id.value
        entity.userId = domain.userId.value
        entity.storyType = domain.storyType.name
        entity.missionId = domain.missionId?.value
        entity.missionTitle = domain.missionTitle?.value
        entity.missionCategory = domain.missionCategory?.name
        entity.storyText = domain.storyText.value
        entity.images = domain.images.map { it.value }.joinToString(",")
        entity.location = domain.location.value
        entity.autoTags = domain.autoTags.map { it.value }.joinToString(",")
        entity.userTags = domain.userTags.map { it.value }.joinToString(",")
        entity.hashTags = domain.getAllTags().map { it.value }.joinToString(",")
        entity.isPublic = domain.isPublic
        entity.likeCount = domain.likes.value
        entity.comments = domain.comments.value
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        
        return entity
    }
    
    fun toDomain(entity: StoryEntity): Story {
        val images = if (entity.images.isBlank()) {
            mutableListOf()
        } else {
            entity.images.split(",").map { ImageUrl(it) }.toMutableList()
        }
        
        val autoTags = if (entity.autoTags.isBlank()) {
            mutableListOf()
        } else {
            entity.autoTags.split(",").map { tag -> 
                val trimmed = tag.trim()
                if (trimmed.startsWith("#")) HashTag(trimmed) else HashTag("#$trimmed")
            }.toMutableList()
        }
        
        val userTags = if (entity.userTags.isBlank()) {
            mutableListOf()
        } else {
            entity.userTags.split(",").map { tag ->
                val trimmed = tag.trim() 
                if (trimmed.startsWith("#")) HashTag(trimmed) else HashTag("#$trimmed")
            }.toMutableList()
        }
        
        return Story(
            id = StoryId(entity.id),
            userId = UserId(entity.userId),
            storyType = StoryType.fromString(entity.storyType),
            missionId = entity.missionId?.let { MissionId(it) },
            missionTitle = entity.missionTitle?.let { MissionTitle(it) },
            missionCategory = entity.missionCategory?.let { MissionCategory.valueOf(it) },
            storyText = StoryText(entity.storyText),
            images = images,
            location = Location(entity.location),
            autoTags = autoTags,
            userTags = userTags,
            isPublic = entity.isPublic,
            likes = LikeCount(entity.likeCount),
            comments = CommentCount(entity.comments),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}