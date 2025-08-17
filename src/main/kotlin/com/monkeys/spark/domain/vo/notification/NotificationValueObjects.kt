package com.monkeys.spark.domain.vo.notification

@JvmInline
value class NotificationId(val value: Long)

enum class NotificationType(val displayName: String) {
    MISSION_STARTED("미션 시작"),
    MISSION_COMPLETED("미션 완료"), 
    LEVEL_UP("레벨 업"),
    ACHIEVEMENT_UNLOCKED("달성 잠금해제"),
    FRIEND_ACTIVITY("친구 활동"),
    SYSTEM_ANNOUNCEMENT("시스템 공지"),
    DAILY_REMINDER("일일 알림")
}

enum class NotificationPriority {
    LOW,
    MEDIUM, 
    HIGH,
    URGENT
}

data class NotificationContent(
    val title: String,
    val message: String,
    val actionUrl: String? = null,
    val imageUrl: String? = null
)