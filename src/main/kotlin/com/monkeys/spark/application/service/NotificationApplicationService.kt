package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.NotificationUseCase
import com.monkeys.spark.application.port.out.NotificationRepository
import com.monkeys.spark.domain.model.Notification
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.NotificationId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationApplicationService(
    private val notificationRepository: NotificationRepository,
    private val webSocketNotificationService: WebSocketNotificationService
) : NotificationUseCase {

    override fun sendNotification(notification: Notification) {
        val savedNotification = notificationRepository.save(notification)
        
        // WebSocket을 통해 실시간 알림 전송
        webSocketNotificationService.sendToUser(
            userId = savedNotification.userId.value,
            notification = savedNotification
        )
    }

    @Transactional(readOnly = true)
    override fun getNotifications(userId: UserId): List<Notification> {
        return notificationRepository.findByUserId(userId)
            .sortedByDescending { it.createdAt }
    }

    @Transactional(readOnly = true)
    override fun getUnreadNotifications(userId: UserId): List<Notification> {
        return notificationRepository.findUnreadByUserId(userId)
            .sortedByDescending { it.createdAt }
    }

    override fun markAsRead(userId: UserId, notificationId: NotificationId): Boolean {
        val notification = notificationRepository.findById(notificationId)
        
        return if (notification != null && notification.userId == userId) {
            notificationRepository.markAsRead(notificationId)
        } else {
            false
        }
    }

    override fun markAllAsRead(userId: UserId): Int {
        return notificationRepository.markAllAsRead(userId)
    }

    @Transactional(readOnly = true)
    override fun getUnreadCount(userId: UserId): Int {
        return notificationRepository.findUnreadByUserId(userId).size
    }
}