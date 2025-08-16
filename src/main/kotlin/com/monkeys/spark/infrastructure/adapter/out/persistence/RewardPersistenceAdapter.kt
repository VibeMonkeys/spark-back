package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.RewardRepository
import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.RewardJpaRepository
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.RewardPersistenceMapper
import org.springframework.stereotype.Component

@Component
class RewardPersistenceAdapter(
    private val rewardJpaRepository: RewardJpaRepository,
    private val rewardMapper: RewardPersistenceMapper
) : RewardRepository {

    override fun save(reward: Reward): Reward {
        val entity = rewardMapper.toEntity(reward)
        val savedEntity = rewardJpaRepository.save(entity)
        return rewardMapper.toDomain(savedEntity)
    }

    override fun findById(id: RewardId): Reward? {
        return rewardJpaRepository.findById(id.value)
            .map { rewardMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAllActive(): List<Reward> {
        return rewardJpaRepository.findActiveRewardsOrderByPopularityAndPoints()
            .map { rewardMapper.toDomain(it) }
    }

    override fun findByCategory(category: RewardCategory): List<Reward> {
        return rewardJpaRepository.findActiveRewardsByCategory(category.name)
            .map { rewardMapper.toDomain(it) }
    }

    override fun findPopularRewards(): List<Reward> {
        return rewardJpaRepository.findPopularRewards()
            .map { rewardMapper.toDomain(it) }
    }

    override fun findPremiumRewards(): List<Reward> {
        return rewardJpaRepository.findPremiumRewards()
            .map { rewardMapper.toDomain(it) }
    }

    override fun findByPointsRange(
        minPoints: Points,
        maxPoints: Points
    ): List<Reward> {
        return rewardJpaRepository.findByRequiredPointsBetween(minPoints.value, maxPoints.value)
            .map { rewardMapper.toDomain(it) }
    }

    override fun findByBrand(brand: BrandName): List<Reward> {
        return rewardJpaRepository.findByBrand(brand.value)
            .map { rewardMapper.toDomain(it) }
    }

    override fun recordExchange(rewardId: RewardId): Reward? {
        return rewardJpaRepository.findById(rewardId.value).map { entity ->
            entity.exchangeCount = (entity.exchangeCount ?: 0) + 1
            entity.lastExchangedAt = java.time.LocalDateTime.now()
            rewardJpaRepository.save(entity)
            rewardMapper.toDomain(entity)
        }.orElse(null)
    }

    override fun deleteById(rewardId: RewardId) {
        rewardJpaRepository.deleteById(rewardId.value)
    }

    override fun updateActiveStatus(
        rewardId: RewardId,
        isActive: Boolean
    ): Reward? {
        return rewardJpaRepository.findById(rewardId.value).map { entity ->
            entity.isActive = isActive
            entity.updatedAt = java.time.LocalDateTime.now()
            rewardJpaRepository.save(entity)
            rewardMapper.toDomain(entity)
        }.orElse(null)
    }
}