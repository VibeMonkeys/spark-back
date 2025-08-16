package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserRewardRepository
import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.UserRewardId
import com.monkeys.spark.domain.vo.reward.RewardStatus
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.UserRewardPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.UserRewardJpaRepository
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

    override fun findById(userRewardId: UserRewardId): UserReward? {
        return userRewardJpaRepository.findById(userRewardId.value)
            .map { userRewardMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: UserId): List<UserReward> {
        return userRewardJpaRepository.findByUserId(userId.value)
            .map { userRewardMapper.toDomain(it) }
    }

    override fun findExpiringWithinDays(days: Int): List<UserReward> {
        val expirationDate = LocalDateTime.now().plusDays(days.toLong())
        return userRewardJpaRepository.findByStatusAndExpiresAtBefore(
            RewardStatus.AVAILABLE.name,
            expirationDate
        ).map { userRewardMapper.toDomain(it) }
    }

    override fun getTotalPointsSpentByUserId(userId: UserId): Points {
        val totalPoints = userRewardJpaRepository.findByUserId(userId.value)
            .sumOf { it.pointsUsed }
        return Points(totalPoints)
    }

    override fun getThisMonthPointsSpentByUserId(userId: UserId): Points {
        val startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)

        val endOfMonth = startOfMonth.plusMonths(1)

        val thisMonthPoints = userRewardJpaRepository.findByUserIdAndExchangedAtBetween(
            userId.value,
            startOfMonth,
            endOfMonth
        ).sumOf { it.pointsUsed }

        return Points(thisMonthPoints)
    }

}