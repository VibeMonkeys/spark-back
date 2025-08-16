package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserRewardRepository
import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.UserRewardJpaRepository
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.UserRewardPersistenceMapper
import com.monkeys.spark.domain.vo.reward.RewardStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserRewardPersistenceAdapter(
    private val userRewardJpaRepository: UserRewardJpaRepository,
    private val userRewardMapper: UserRewardPersistenceMapper
) : UserRewardRepository {

    override fun save(userReward: UserReward): UserReward {
        val entity = userRewardMapper.toEntity(userReward)
        val savedEntity = userRewardJpaRepository.save(entity)
        return userRewardMapper.toDomain(savedEntity)
    }

    override fun findById(userRewardId: String): UserReward? {
        return userRewardJpaRepository.findById(userRewardId)
            .map { userRewardMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: UserId): List<UserReward> {
        return userRewardJpaRepository.findByUserId(userId.value)
            .map { userRewardMapper.toDomain(it) }
    }

    override fun findAvailableByUserId(userId: UserId): List<UserReward> {
        return userRewardJpaRepository.findByUserIdAndStatus(userId.value, RewardStatus.AVAILABLE.name)
            .map { userRewardMapper.toDomain(it) }
    }

    override fun findUsedByUserId(userId: UserId): List<UserReward> {
        return userRewardJpaRepository.findByUserIdAndStatus(userId.value, RewardStatus.USED.name)
            .map { userRewardMapper.toDomain(it) }
    }

    override fun findExpiredByUserId(userId: UserId): List<UserReward> {
        return userRewardJpaRepository.findByUserIdAndStatus(userId.value, RewardStatus.EXPIRED.name)
            .map { userRewardMapper.toDomain(it) }
    }

    override fun findByExchangeCode(exchangeCode: String): UserReward? {
        return userRewardJpaRepository.findByExchangeCode(exchangeCode)
            ?.let { userRewardMapper.toDomain(it) }
    }

    override fun findExpiringWithinDays(days: Int): List<UserReward> {
        val expirationDate = LocalDateTime.now().plusDays(days.toLong())
        return userRewardJpaRepository.findByStatusAndExpiresAtBefore(
            RewardStatus.AVAILABLE.name,
            expirationDate
        ).map { userRewardMapper.toDomain(it) }
    }

    override fun markAsUsed(userRewardId: String): UserReward? {
        return userRewardJpaRepository.findById(userRewardId).map { entity ->
            entity.status = RewardStatus.USED.name
            entity.usedAt = LocalDateTime.now()
            entity.updatedAt = LocalDateTime.now()
            userRewardJpaRepository.save(entity)
            userRewardMapper.toDomain(entity)
        }.orElse(null)
    }

    override fun markExpiredRewards(): List<UserReward> {
        val now = LocalDateTime.now()
        val expiredRewards = userRewardJpaRepository.findByStatusAndExpiresAtBefore(
            RewardStatus.AVAILABLE.name,
            now
        )

        return expiredRewards.map { entity ->
            entity.status = RewardStatus.EXPIRED.name
            entity.updatedAt = now
            userRewardJpaRepository.save(entity)
            userRewardMapper.toDomain(entity)
        }
    }

    override fun getTotalPointsSpentByUserId(userId: UserId): Points {
        val totalPoints = userRewardJpaRepository.findByUserId(userId.value)
            .sumOf { it.pointsUsed }
        return Points(totalPoints)
    }

    override fun getThisMonthPointsSpentByUserId(userId: UserId): Points {
        val startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        val endOfMonth = startOfMonth.plusMonths(1)

        val thisMonthPoints = userRewardJpaRepository.findByUserIdAndExchangedAtBetween(
            userId.value,
            startOfMonth,
            endOfMonth
        ).sumOf { it.pointsUsed }

        return Points(thisMonthPoints)
    }

    override fun getExchangeCountByRewardId(rewardId: RewardId): Int {
        return userRewardJpaRepository.countByRewardId(rewardId.value)
    }

    override fun deleteById(userRewardId: String) {
        userRewardJpaRepository.deleteById(userRewardId)
    }

    override fun deleteByUserId(userId: UserId) {
        userRewardJpaRepository.deleteByUserId(userId.value)
    }
}