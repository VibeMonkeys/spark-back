package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import com.monkeys.spark.domain.vo.achievement.AchievementType
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 사용자 업적 JPA 엔티티
 */
@Entity
@Table(
    name = "user_achievements",
    indexes = [
        Index(name = "idx_user_achievements_user_id", columnList = "user_id"),
        Index(name = "idx_user_achievements_achievement_type", columnList = "achievement_type"),
        Index(name = "idx_user_achievements_unlocked_at", columnList = "unlocked_at")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_achievement", columnNames = ["user_id", "achievement_type"])
    ]
)
class UserAchievementEntity : BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L

    @Enumerated(EnumType.STRING)
    @Column(name = "achievement_type", nullable = false, length = 50)
    var achievementType: AchievementType = AchievementType.FIRST_MISSION

    @Column(name = "unlocked_at", nullable = false)
    var unlockedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "progress", nullable = false)
    var progress: Int = 0

    @Column(name = "is_notified", nullable = false)
    var isNotified: Boolean = false

    constructor()

    constructor(
        userId: Long,
        achievementType: AchievementType,
        unlockedAt: LocalDateTime,
        progress: Int,
        isNotified: Boolean
    ) {
        this.userId = userId
        this.achievementType = achievementType
        this.unlockedAt = unlockedAt
        this.progress = progress
        this.isNotified = isNotified
    }
}