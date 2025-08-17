package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.Notification
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.NotificationEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.NotificationPriorityEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.NotificationTypeEntity
import org.springframework.stereotype.Component

@Component
class NotificationPersistenceMapper {

    fun toDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = if (entity.id != 0L) NotificationId(entity.id) else null,
            userId = UserId(entity.userId),
            type = mapTypeToDomain(entity.type),
            priority = mapPriorityToDomain(entity.priority),
            content = NotificationContent(
                title = entity.title,
                message = entity.message,
                actionUrl = entity.actionUrl,
                imageUrl = entity.imageUrl
            ),
            isRead = entity.isRead,
            createdAt = entity.createdAt,
            readAt = entity.readAt
        )
    }

    fun toEntity(domain: Notification): NotificationEntity {
        return NotificationEntity().apply {
            id = domain.id?.value ?: 0L
            userId = domain.userId.value
            type = mapTypeToEntity(domain.type)
            priority = mapPriorityToEntity(domain.priority)
            title = domain.content.title
            message = domain.content.message
            actionUrl = domain.content.actionUrl
            imageUrl = domain.content.imageUrl
            isRead = domain.isRead
            readAt = domain.readAt
        }
    }

    private fun mapTypeToDomain(entityType: NotificationTypeEntity): NotificationType {
        return when (entityType) {
            NotificationTypeEntity.MISSION_STARTED -> NotificationType.MISSION_STARTED
            NotificationTypeEntity.MISSION_COMPLETED -> NotificationType.MISSION_COMPLETED
            NotificationTypeEntity.LEVEL_UP -> NotificationType.LEVEL_UP
            NotificationTypeEntity.ACHIEVEMENT_UNLOCKED -> NotificationType.ACHIEVEMENT_UNLOCKED
            NotificationTypeEntity.FRIEND_ACTIVITY -> NotificationType.FRIEND_ACTIVITY
            NotificationTypeEntity.SYSTEM_ANNOUNCEMENT -> NotificationType.SYSTEM_ANNOUNCEMENT
            NotificationTypeEntity.DAILY_REMINDER -> NotificationType.DAILY_REMINDER
        }
    }

    private fun mapTypeToEntity(domainType: NotificationType): NotificationTypeEntity {
        return when (domainType) {
            NotificationType.MISSION_STARTED -> NotificationTypeEntity.MISSION_STARTED
            NotificationType.MISSION_COMPLETED -> NotificationTypeEntity.MISSION_COMPLETED
            NotificationType.LEVEL_UP -> NotificationTypeEntity.LEVEL_UP
            NotificationType.ACHIEVEMENT_UNLOCKED -> NotificationTypeEntity.ACHIEVEMENT_UNLOCKED
            NotificationType.FRIEND_ACTIVITY -> NotificationTypeEntity.FRIEND_ACTIVITY
            NotificationType.SYSTEM_ANNOUNCEMENT -> NotificationTypeEntity.SYSTEM_ANNOUNCEMENT
            NotificationType.DAILY_REMINDER -> NotificationTypeEntity.DAILY_REMINDER
        }
    }

    private fun mapPriorityToDomain(entityPriority: NotificationPriorityEntity): NotificationPriority {
        return when (entityPriority) {
            NotificationPriorityEntity.LOW -> NotificationPriority.LOW
            NotificationPriorityEntity.MEDIUM -> NotificationPriority.MEDIUM
            NotificationPriorityEntity.HIGH -> NotificationPriority.HIGH
            NotificationPriorityEntity.URGENT -> NotificationPriority.URGENT
        }
    }

    private fun mapPriorityToEntity(domainPriority: NotificationPriority): NotificationPriorityEntity {
        return when (domainPriority) {
            NotificationPriority.LOW -> NotificationPriorityEntity.LOW
            NotificationPriority.MEDIUM -> NotificationPriorityEntity.MEDIUM
            NotificationPriority.HIGH -> NotificationPriorityEntity.HIGH
            NotificationPriority.URGENT -> NotificationPriorityEntity.URGENT
        }
    }
}