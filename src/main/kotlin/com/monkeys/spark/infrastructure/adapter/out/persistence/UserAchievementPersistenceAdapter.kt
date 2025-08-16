package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserAchievementRepository
import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.achievement.AchievementType
import com.monkeys.spark.domain.vo.common.UserId
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

    override fun findByUserId(userId: UserId): List<UserAchievement> {
        val entities = jpaRepository.findByUserIdOrderByUnlockedAtDesc(userId.value)
        return mapper.toDomainList(entities)
    }

    override fun findByUserIdAndAchievementType(
        userId: UserId,
        achievementType: String
    ): UserAchievement? {
        val achievementTypeEnum = try {
            AchievementType.valueOf(achievementType)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val entity = jpaRepository.findByUserIdAndAchievementType(userId.value, achievementTypeEnum)
        return entity?.let { mapper.toDomain(it) }
    }

    override fun countUnlockedByUserId(userId: UserId): Int {
        return jpaRepository.countUnlockedByUserId(userId.value)
    }

    override fun getAchievementStatistics(): Map<String, Int> {
        val statistics = jpaRepository.getAchievementStatistics()
        return statistics.associate {
            it.getAchievementType().name to it.getCount().toInt()
        }
    }

}