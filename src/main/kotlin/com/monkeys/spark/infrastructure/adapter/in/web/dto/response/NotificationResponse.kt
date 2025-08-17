package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import java.time.LocalDateTime

data class NotificationResponse(
    val id: Long,
    val type: String,
    val priority: String,
    val title: String,
    val message: String,
    val actionUrl: String?,
    val imageUrl: String?,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
    val readAt: LocalDateTime?
)