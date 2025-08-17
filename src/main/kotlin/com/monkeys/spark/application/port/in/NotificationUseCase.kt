package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.domain.model.Notification
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.*

interface NotificationUseCase {
    fun sendNotification(notification: Notification)
    fun getNotifications(userId: UserId): List<Notification>
    fun getUnreadNotifications(userId: UserId): List<Notification>
    fun markAsRead(userId: UserId, notificationId: NotificationId): Boolean
    fun markAllAsRead(userId: UserId): Int
    fun getUnreadCount(userId: UserId): Int
    fun deleteNotification(userId: UserId, notificationId: NotificationId): Boolean
    fun deleteAllNotifications(userId: UserId): Int
}

data class SendNotificationCommand(
    val userId: UserId,
    val type: NotificationType,
    val priority: NotificationPriority,
    val content: NotificationContent
)