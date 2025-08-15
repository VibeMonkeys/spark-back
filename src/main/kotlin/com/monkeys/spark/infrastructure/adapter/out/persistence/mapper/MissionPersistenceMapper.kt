package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.MissionEntity
import org.springframework.stereotype.Component

@Component
class MissionPersistenceMapper {
    
    fun toEntity(domain: Mission): MissionEntity {
        val entity = MissionEntity()
        entity.id = domain.id.value
        entity.userId = domain.userId.value
        entity.title = domain.title.value
        entity.description = domain.description.value
        entity.detailedDescription = domain.detailedDescription.value
        entity.category = domain.category.name
        entity.difficulty = domain.difficulty.name
        entity.status = domain.status.name
        entity.rewardPoints = domain.rewardPoints.value
        entity.estimatedMinutes = domain.estimatedMinutes
        entity.imageUrl = domain.imageUrl.value
        entity.tips = domain.tips.joinToString(",")
        entity.progress = domain.progress
        entity.isTemplate = domain.isTemplate
        entity.assignedAt = domain.assignedAt
        entity.startedAt = domain.startedAt
        entity.completedAt = domain.completedAt
        entity.expiresAt = domain.expiresAt
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        
        return entity
    }
    
    fun toDomain(entity: MissionEntity): Mission {
        val tips = if (entity.tips?.isBlank() != false) {
            mutableListOf()
        } else {
            entity.tips.split(",").toMutableList()
        }
        
        return Mission(
            id = MissionId(entity.id),
            userId = UserId(entity.userId),
            title = MissionTitle(entity.title),
            description = MissionDescription(entity.description),
            detailedDescription = MissionDescription(entity.detailedDescription),
            category = MissionCategory.valueOf(entity.category),
            difficulty = MissionDifficulty.valueOf(entity.difficulty),
            status = MissionStatus.valueOf(entity.status),
            rewardPoints = Points(entity.rewardPoints),
            estimatedMinutes = entity.estimatedMinutes,
            imageUrl = ImageUrl(entity.imageUrl?.takeIf { it.isNotBlank() } ?: "https://example.com/default.jpg"),
            tips = tips,
            progress = entity.progress,
            isTemplate = entity.isTemplate,
            assignedAt = entity.assignedAt,
            startedAt = entity.startedAt,
            completedAt = entity.completedAt,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}