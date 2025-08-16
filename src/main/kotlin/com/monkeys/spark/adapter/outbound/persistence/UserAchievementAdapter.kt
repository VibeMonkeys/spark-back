package com.monkeys.spark.adapter.outbound.persistence

import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.achievement.AchievementType
import com.monkeys.spark.port.outbound.UserAchievementPort
import org.springframework.stereotype.Component

/**
 * 사용자 업적 데이터 접근 어댑터
 * 헥사고날 아키텍처의 아웃바운드 어댑터
 */
@Component
class UserAchievementAdapter(
    private val repository: UserAchievementRepository
) : UserAchievementPort {
    
    override fun save(userAchievement: UserAchievement): UserAchievement {
        // TODO: 실제 Entity 및 Repository 구현 필요
        return userAchievement
    }
    
    override fun findByUserId(userId: String): List<UserAchievement> {
        // TODO: 실제 구현 필요
        return emptyList()
    }
    
    override fun findByUserIdAndAchievementType(userId: String, achievementType: String): UserAchievement? {
        // TODO: 실제 구현 필요
        return null
    }
    
    override fun countUnlockedByUserId(userId: String): Int {
        // TODO: 실제 구현 필요
        return 0
    }
    
    override fun getAchievementStatistics(): Map<String, Int> {
        // TODO: 실제 구현 필요
        return emptyMap()
    }
}