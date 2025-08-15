package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.AchievementUseCase
import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.service.AchievementService
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.port.outbound.UserAchievementPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 업적 시스템 애플리케이션 서비스
 */
@Service
@Transactional
class AchievementApplicationService(
    private val achievementService: AchievementService,
    private val userAchievementPort: UserAchievementPort
) : AchievementUseCase {
    
    @Transactional
    override fun getUserAchievements(userId: UserId): List<UserAchievement> {
        return achievementService.getUserAchievements(userId.value)
    }
    
    @Transactional(readOnly = true)
    override fun getUserAchievementCount(userId: UserId): Int {
        return userAchievementPort.countUnlockedByUserId(userId.value)
    }
    
    @Transactional(readOnly = true)
    override fun getAchievementStatistics(): Map<String, Int> {
        return userAchievementPort.getAchievementStatistics()
    }
}