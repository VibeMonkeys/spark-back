package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.Notification
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.NotificationId
import com.monkeys.spark.domain.vo.notification.NotificationType

interface NotificationRepository {
    fun save(notification: Notification): Notification
    fun findById(id: NotificationId): Notification?
    fun findByUserId(userId: UserId): List<Notification>
    fun findUnreadByUserId(userId: UserId): List<Notification>
    fun findByUserIdAndType(userId: UserId, type: NotificationType): List<Notification>
    fun markAsRead(id: NotificationId): Boolean
    fun markAllAsRead(userId: UserId): Int
    fun deleteExpired(): Int
    fun deleteByUserId(userId: UserId): Int
}