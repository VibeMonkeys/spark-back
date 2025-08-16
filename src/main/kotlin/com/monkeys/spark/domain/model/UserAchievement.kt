package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.achievement.AchievementType
import java.time.LocalDateTime

/**
 * 사용자 업적 도메인 모델
 * DDD 원칙에 따른 도메인 객체로 업적 획득과 관련된 비즈니스 로직을 포함
 */
data class UserAchievement(
    val userId: String,
    val achievementType: AchievementType,
    val unlockedAt: LocalDateTime,
    val progress: Int = 100, // 0-100, 업적 달성 진행도
    val isNotified: Boolean = false // 사용자에게 알림이 전송되었는지 여부
) {

    companion object {
        /**
         * 새로운 업적 획득 생성
         */
        fun unlock(userId: String, achievementType: AchievementType): UserAchievement {
            return UserAchievement(
                userId = userId,
                achievementType = achievementType,
                unlockedAt = LocalDateTime.now(),
                progress = 100,
                isNotified = false
            )
        }

        /**
         * 진행 중인 업적 생성 (조건을 부분적으로 달성)
         */
        fun inProgress(userId: String, achievementType: AchievementType, progress: Int): UserAchievement {
            require(progress in 0..99) { "진행 중인 업적의 progress는 0-99 사이여야 합니다" }

            return UserAchievement(
                userId = userId,
                achievementType = achievementType,
                unlockedAt = LocalDateTime.now(), // 최초 진행 시작 시간
                progress = progress,
                isNotified = false
            )
        }
    }

    /**
     * 업적이 완전히 달성되었는지 확인
     */
    fun isUnlocked(): Boolean = progress >= 100

}