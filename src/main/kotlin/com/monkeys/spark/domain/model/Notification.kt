package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.*
import java.time.LocalDateTime

data class Notification(
    var id: NotificationId? = null,
    var userId: UserId,
    var type: NotificationType,
    var priority: NotificationPriority,
    var content: NotificationContent,
    var isRead: Boolean = false,
    var createdAt: LocalDateTime,
    var readAt: LocalDateTime? = null
) {
    fun markAsRead() {
        isRead = true
        readAt = LocalDateTime.now()
    }
    
    fun isExpired(expirationHours: Long = 72): Boolean {
        return createdAt.plusHours(expirationHours).isBefore(LocalDateTime.now())
    }
    
    companion object {
        fun create(
            userId: UserId,
            type: NotificationType,
            priority: NotificationPriority,
            content: NotificationContent
        ): Notification {
            return Notification(
                userId = userId,
                type = type,
                priority = priority,
                content = content,
                createdAt = LocalDateTime.now()
            )
        }
        
        fun missionStarted(userId: UserId, missionTitle: String): Notification {
            return create(
                userId = userId,
                type = NotificationType.MISSION_STARTED,
                priority = NotificationPriority.MEDIUM,
                content = NotificationContent(
                    title = "미션 시작",
                    message = "\"${missionTitle}\" 미션을 시작했습니다!",
                    actionUrl = "/missions/current"
                )
            )
        }
        
        fun missionCompleted(userId: UserId, missionTitle: String, pointsEarned: Int): Notification {
            return create(
                userId = userId,
                type = NotificationType.MISSION_COMPLETED,
                priority = NotificationPriority.HIGH,
                content = NotificationContent(
                    title = "미션 완료!",
                    message = "\"${missionTitle}\" 미션을 완료하고 ${pointsEarned}포인트를 획득했습니다!",
                    actionUrl = "/profile"
                )
            )
        }
        
        fun levelUp(userId: UserId, newLevel: Int, newLevelTitle: String): Notification {
            return create(
                userId = userId,
                type = NotificationType.LEVEL_UP,
                priority = NotificationPriority.HIGH,
                content = NotificationContent(
                    title = "레벨업!",
                    message = "축하합니다! 레벨 ${newLevel} (${newLevelTitle})이 되었습니다!",
                    actionUrl = "/profile"
                )
            )
        }
        
        fun achievementUnlocked(userId: UserId, achievementName: String): Notification {
            return create(
                userId = userId,
                type = NotificationType.ACHIEVEMENT_UNLOCKED,
                priority = NotificationPriority.HIGH,
                content = NotificationContent(
                    title = "달성 잠금해제!",
                    message = "\"${achievementName}\" 달성을 잠금해제했습니다!",
                    actionUrl = "/achievements"
                )
            )
        }
    }
}