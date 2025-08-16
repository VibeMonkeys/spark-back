package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserRewardEntity
import org.springframework.stereotype.Component

@Component
class UserRewardPersistenceMapper {
    
    fun toEntity(domain: UserReward): UserRewardEntity {
        val entity = UserRewardEntity()
        entity.id = domain.id.value
        entity.userId = domain.userId.value
        entity.rewardId = domain.rewardId.value
        entity.rewardTitle = domain.rewardTitle.value
        entity.rewardBrand = domain.rewardBrand.value
        entity.pointsUsed = domain.pointsUsed.value
        entity.exchangeCode = domain.exchangeCode
        entity.status = domain.status.name
        entity.exchangedAt = domain.usedAt // 사용일을 교환일로 매핑
        entity.usedAt = domain.usedAt
        entity.expiresAt = domain.expiresAt
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.createdAt // 생성일을 업데이트일로 초기 설정
        return entity
    }
    
    fun toDomain(entity: UserRewardEntity): UserReward {
        return UserReward(
            id = UserRewardId(entity.id),
            userId = UserId(entity.userId),
            rewardId = RewardId(entity.rewardId),
            rewardTitle = RewardTitle(entity.rewardTitle),
            rewardBrand = BrandName(entity.rewardBrand),
            pointsUsed = Points(entity.pointsUsed),
            exchangeCode = entity.exchangeCode,
            status = RewardStatus.valueOf(entity.status),
            usedAt = entity.usedAt,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt
        )
    }
}