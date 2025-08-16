package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserAchievementRepository
import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.achievement.AchievementType
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.UserAchievementPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.UserAchievementJpaRepository
import org.springframework.stereotype.Repository

/**
 * UserAchievement 영속성 어댑터
 * 헥사고날 아키텍처의 아웃바운드 어댑터로 UserAchievementRepository 포트를 구현
 */
@Repository
class UserAchievementPersistenceAdapter(
    private val jpaRepository: UserAchievementJpaRepository,
    private val mapper: UserAchievementPersistenceMapper
) : UserAchievementRepository {
    
    override fun save(userAchievement: UserAchievement): UserAchievement {
        val entity = mapper.toEntity(userAchievement)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }
    
    override fun findByUserId(userId: String): List<UserAchievement> {
        val entities = jpaRepository.findByUserIdOrderByUnlockedAtDesc(userId)
        return mapper.toDomainList(entities)
    }
    
    override fun findByUserIdAndAchievementType(userId: String, achievementType: String): UserAchievement? {
        val achievementTypeEnum = try {
            AchievementType.valueOf(achievementType)
        } catch (e: IllegalArgumentException) {
            return null
        }
        
        val entity = jpaRepository.findByUserIdAndAchievementType(userId, achievementTypeEnum)
        return entity?.let { mapper.toDomain(it) }
    }
    
    override fun countUnlockedByUserId(userId: String): Int {
        return jpaRepository.countUnlockedByUserId(userId)
    }
    
    override fun getAchievementStatistics(): Map<String, Int> {
        val statistics = jpaRepository.getAchievementStatistics()
        return statistics.associate { 
            it.getAchievementType().name to it.getCount().toInt() 
        }
    }
    
    /**
     * 추가 메서드: 특정 업적이 이미 달성되었는지 확인
     */
    fun existsByUserIdAndAchievementType(userId: String, achievementType: AchievementType): Boolean {
        return jpaRepository.existsByUserIdAndAchievementType(userId, achievementType)
    }
    
    /**
     * 추가 메서드: 알림이 전송되지 않은 업적들 조회 (알림 시스템용)
     */
    fun findUnnotifiedAchievements(): List<UserAchievement> {
        val entities = jpaRepository.findUnnotifiedAchievements()
        return mapper.toDomainList(entities)
    }
    
    /**
     * 추가 메서드: 업적 알림 상태 업데이트
     */
    fun markAsNotified(userAchievement: UserAchievement): UserAchievement {
        val updatedAchievement = userAchievement.copy(isNotified = true)
        return save(updatedAchievement)
    }
}