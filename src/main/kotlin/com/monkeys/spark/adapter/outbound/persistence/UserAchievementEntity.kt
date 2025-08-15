package com.monkeys.spark.adapter.outbound.persistence

import com.monkeys.spark.domain.model.UserAchievement
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
    ]
)
data class UserAchievementEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(name = "user_id", nullable = false, length = 100)
    val userId: String,
    
    @Column(name = "achievement_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    val achievementType: AchievementType,
    
    @Column(name = "unlocked_at", nullable = false)
    val unlockedAt: LocalDateTime,
    
    @Column(name = "progress", nullable = false)
    val progress: Int = 100,
    
    @Column(name = "is_notified", nullable = false)
    val isNotified: Boolean = false,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    /**
     * 도메인 모델로 변환
     */
    fun toDomain(): UserAchievement {
        return UserAchievement(
            userId = userId,
            achievementType = achievementType,
            unlockedAt = unlockedAt,
            progress = progress,
            isNotified = isNotified
        )
    }
    
    companion object {
        /**
         * 도메인 모델에서 엔티티로 변환
         */
        fun fromDomain(userAchievement: UserAchievement): UserAchievementEntity {
            return UserAchievementEntity(
                userId = userAchievement.userId,
                achievementType = userAchievement.achievementType,
                unlockedAt = userAchievement.unlockedAt,
                progress = userAchievement.progress,
                isNotified = userAchievement.isNotified
            )
        }
    }
}