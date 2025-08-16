package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserAchievementEntity
import org.springframework.stereotype.Component

/**
 * UserAchievement 도메인 모델과 JPA 엔티티 간의 매핑을 담당하는 매퍼
 */
@Component
class UserAchievementPersistenceMapper {

    /**
     * 도메인 모델을 JPA 엔티티로 변환
     */
    fun toEntity(domain: UserAchievement): UserAchievementEntity {
        return UserAchievementEntity(
            userId = domain.userId.value,
            achievementType = domain.achievementType,
            unlockedAt = domain.unlockedAt,
            progress = domain.progress,
            isNotified = domain.isNotified
        )
    }

    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    fun toDomain(entity: UserAchievementEntity): UserAchievement {
        return UserAchievement(
            userId = UserId(entity.userId),
            achievementType = entity.achievementType,
            unlockedAt = entity.unlockedAt,
            progress = entity.progress,
            isNotified = entity.isNotified
        )
    }

    /**
     * 여러 엔티티를 도메인 모델 리스트로 변환
     */
    fun toDomainList(entities: List<UserAchievementEntity>): List<UserAchievement> {
        return entities.map { toDomain(it) }
    }

}