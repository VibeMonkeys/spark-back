package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.NotificationUseCase
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.notification.NotificationId
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.NotificationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationUseCase: NotificationUseCase
) {

    @GetMapping
    fun getNotifications(
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val userId = UserId(userId)
        val notifications = notificationUseCase.getNotifications(userId)
        
        val responseList = notifications.map { notification ->
            NotificationResponse(
                id = notification.id?.value ?: 0L,
                type = notification.type.name,
                priority = notification.priority.name,
                title = notification.content.title,
                message = notification.content.message,
                actionUrl = notification.content.actionUrl,
                imageUrl = notification.content.imageUrl,
                isRead = notification.isRead,
                createdAt = notification.createdAt,
                readAt = notification.readAt
            )
        }
        
        return ResponseEntity.ok(ApiResponse.success(responseList, "알림 목록을 조회했습니다."))
    }

    @GetMapping("/unread")
    fun getUnreadNotifications(
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        val userId = UserId(userId)
        val notifications = notificationUseCase.getUnreadNotifications(userId)
        
        val responseList = notifications.map { notification ->
            NotificationResponse(
                id = notification.id?.value ?: 0L,
                type = notification.type.name,
                priority = notification.priority.name,
                title = notification.content.title,
                message = notification.content.message,
                actionUrl = notification.content.actionUrl,
                imageUrl = notification.content.imageUrl,
                isRead = notification.isRead,
                createdAt = notification.createdAt,
                readAt = notification.readAt
            )
        }
        
        return ResponseEntity.ok(ApiResponse.success(responseList, "읽지 않은 알림 목록을 조회했습니다."))
    }

    @GetMapping("/unread/count")
    fun getUnreadCount(
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<Int>> {
        val userId = UserId(userId)
        val count = notificationUseCase.getUnreadCount(userId)
        
        return ResponseEntity.ok(ApiResponse.success(count, "읽지 않은 알림 개수를 조회했습니다."))
    }

    @PutMapping("/{notificationId}/read")
    fun markAsRead(
        @PathVariable notificationId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<Boolean>> {
        val userId = UserId(userId)
        val success = notificationUseCase.markAsRead(userId, NotificationId(notificationId))
        
        return if (success) {
            ResponseEntity.ok(ApiResponse.success(true, "알림을 읽음으로 표시했습니다."))
        } else {
            ResponseEntity.badRequest().body(ApiResponse.error("알림을 찾을 수 없거나 권한이 없습니다.", "NOTIFICATION_NOT_FOUND"))
        }
    }

    @PutMapping("/read-all")
    fun markAllAsRead(
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<Int>> {
        val userId = UserId(userId)
        val count = notificationUseCase.markAllAsRead(userId)
        
        return ResponseEntity.ok(ApiResponse.success(count, "모든 알림을 읽음으로 표시했습니다."))
    }
}