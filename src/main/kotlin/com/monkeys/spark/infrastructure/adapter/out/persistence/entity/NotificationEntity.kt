package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
class NotificationEntity : BaseEntity() {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L
    
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    var type: NotificationTypeEntity = NotificationTypeEntity.SYSTEM_ANNOUNCEMENT
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    var priority: NotificationPriorityEntity = NotificationPriorityEntity.MEDIUM
    
    @Column(name = "title", nullable = false, length = 200)
    var title: String = ""
    
    @Column(name = "message", nullable = false, length = 1000)
    var message: String = ""
    
    @Column(name = "action_url", length = 500)
    var actionUrl: String? = null
    
    @Column(name = "image_url", length = 500)
    var imageUrl: String? = null
    
    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false
    
    @Column(name = "read_at")
    var readAt: LocalDateTime? = null
}

enum class NotificationTypeEntity {
    MISSION_STARTED,
    MISSION_COMPLETED,
    LEVEL_UP,
    ACHIEVEMENT_UNLOCKED,
    FRIEND_ACTIVITY,
    SYSTEM_ANNOUNCEMENT,
    DAILY_REMINDER
}

enum class NotificationPriorityEntity {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}