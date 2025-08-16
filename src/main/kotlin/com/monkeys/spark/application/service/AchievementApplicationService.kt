package com.monkeys.spark.application.service

import com.monkeys.spark.application.coordinator.AchievementCoordinator
import com.monkeys.spark.application.port.`in`.AchievementUseCase
import com.monkeys.spark.application.port.out.UserAchievementRepository
import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 업적 시스템 애플리케이션 서비스
 */
@Service
@Transactional
class AchievementApplicationService(
    private val achievementCoordinator: AchievementCoordinator,
    private val userAchievementRepository: UserAchievementRepository
) : AchievementUseCase {

    @Transactional
    override fun getUserAchievements(userId: UserId): List<UserAchievement> {
        return achievementCoordinator.getUserAchievements(userId)
    }

    @Transactional(readOnly = true)
    override fun getUserAchievementCount(userId: UserId): Int {
        return userAchievementRepository.countUnlockedByUserId(userId)
    }

    @Transactional(readOnly = true)
    override fun getAchievementStatistics(): Map<String, Int> {
        return userAchievementRepository.getAchievementStatistics()
    }

}