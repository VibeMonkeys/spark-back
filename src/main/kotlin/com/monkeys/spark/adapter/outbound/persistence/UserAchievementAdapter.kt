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
        val entity = UserAchievementEntity.fromDomain(userAchievement)
        val savedEntity = repository.save(entity)
        return savedEntity.toDomain()
    }
    
    override fun findByUserId(userId: String): List<UserAchievement> {
        return repository.findByUserId(userId)
            .map { it.toDomain() }
    }
    
    override fun findByUserIdAndAchievementType(userId: String, achievementType: String): UserAchievement? {
        val enumType = try {
            AchievementType.valueOf(achievementType.uppercase())
        } catch (e: IllegalArgumentException) {
            return null
        }
        
        return repository.findByUserIdAndAchievementType(userId, enumType)
            ?.toDomain()
    }
    
    override fun countUnlockedByUserId(userId: String): Int {
        return repository.countUnlockedByUserId(userId)
    }
    
    override fun getAchievementStatistics(): Map<String, Int> {
        return repository.getAchievementStatistics()
            .associate { 
                it.getAchievementType().name to it.getCount().toInt() 
            }
    }
}