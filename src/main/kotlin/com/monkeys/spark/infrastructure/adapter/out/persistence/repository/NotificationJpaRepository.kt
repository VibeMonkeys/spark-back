package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.NotificationEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.NotificationTypeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface NotificationJpaRepository : JpaRepository<NotificationEntity, Long> {
    
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<NotificationEntity>
    
    fun findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId: Long): List<NotificationEntity>
    
    fun findByUserIdAndTypeOrderByCreatedAtDesc(userId: Long, type: NotificationTypeEntity): List<NotificationEntity>
    
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :id")
    fun markAsRead(id: Long, readAt: LocalDateTime): Int
    
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = false")
    fun markAllAsRead(userId: Long, readAt: LocalDateTime): Int
    
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :cutoffDate")
    fun deleteExpiredNotifications(cutoffDate: LocalDateTime): Int
    
    fun deleteByUserId(userId: Long): Int
    
    fun countByUserIdAndIsReadFalse(userId: Long): Int
}