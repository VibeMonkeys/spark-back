package com.monkeys.spark.application.service

import com.monkeys.spark.domain.model.Notification
import com.monkeys.spark.infrastructure.adapter.`in`.web.websocket.NotificationWebSocketHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WebSocketNotificationService(
    private val webSocketHandler: NotificationWebSocketHandler
) {
    
    private val logger = LoggerFactory.getLogger(WebSocketNotificationService::class.java)

    fun sendToUser(userId: Long, notification: Notification) {
        try {
            val notificationDto = mapOf(
                "id" to notification.id.value,
                "type" to notification.type.name,
                "priority" to notification.priority.name,
                "title" to notification.content.title,
                "message" to notification.content.message,
                "actionUrl" to notification.content.actionUrl,
                "imageUrl" to notification.content.imageUrl,
                "isRead" to notification.isRead,
                "createdAt" to notification.createdAt.toString()
            )
            
            webSocketHandler.sendNotificationToUser(userId, notificationDto)
            logger.info("WebSocket notification sent to user: $userId, type: ${notification.type}")
        } catch (e: Exception) {
            logger.error("Failed to send WebSocket notification to user $userId: ${e.message}")
        }
    }

    fun isUserConnected(userId: Long): Boolean {
        return webSocketHandler.isUserConnected(userId)
    }

    fun getConnectedUserCount(): Int {
        return webSocketHandler.getConnectedUserCount()
    }
}