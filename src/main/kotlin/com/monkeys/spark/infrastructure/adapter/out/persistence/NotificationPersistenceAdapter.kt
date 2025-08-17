package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.NotificationRepository
import com.monkeys.spark.domain.model.Notification
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.NotificationId
import com.monkeys.spark.domain.vo.notification.NotificationType
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.NotificationTypeEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.NotificationPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.NotificationJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class NotificationPersistenceAdapter(
    private val jpaRepository: NotificationJpaRepository,
    private val mapper: NotificationPersistenceMapper
) : NotificationRepository {

    override fun save(notification: Notification): Notification {
        val entity = mapper.toEntity(notification)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findById(id: NotificationId): Notification? {
        return jpaRepository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: UserId): List<Notification> {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId.value)
            .map { mapper.toDomain(it) }
    }

    override fun findUnreadByUserId(userId: UserId): List<Notification> {
        return jpaRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId.value)
            .map { mapper.toDomain(it) }
    }

    override fun findByUserIdAndType(userId: UserId, type: NotificationType): List<Notification> {
        val entityType = mapTypeToEntity(type)
        return jpaRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId.value, entityType)
            .map { mapper.toDomain(it) }
    }

    override fun markAsRead(id: NotificationId): Boolean {
        val updatedRows = jpaRepository.markAsRead(id.value, LocalDateTime.now())
        return updatedRows > 0
    }

    override fun markAllAsRead(userId: UserId): Int {
        return jpaRepository.markAllAsRead(userId.value, LocalDateTime.now())
    }

    override fun deleteById(id: NotificationId): Boolean {
        return if (jpaRepository.existsById(id.value)) {
            jpaRepository.deleteById(id.value)
            true
        } else {
            false
        }
    }

    override fun deleteAllByUserId(userId: UserId): Int {
        return jpaRepository.deleteByUserId(userId.value)
    }

    override fun deleteExpired(): Int {
        val cutoffDate = LocalDateTime.now().minusHours(72) // 3일 후 자동 삭제
        return jpaRepository.deleteExpiredNotifications(cutoffDate)
    }

    override fun deleteByUserId(userId: UserId): Int {
        return jpaRepository.deleteByUserId(userId.value)
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
}