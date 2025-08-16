package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserRewardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRewardJpaRepository : JpaRepository<UserRewardEntity, Long> {

    fun findByUserId(userId: Long): List<UserRewardEntity>

    fun findByUserIdAndStatus(userId: Long, status: String): List<UserRewardEntity>

    fun findByExchangeCode(exchangeCode: String): UserRewardEntity?

    fun findByStatusAndExpiresAtBefore(status: String, expirationDate: LocalDateTime): List<UserRewardEntity>

    fun findByUserIdAndExchangedAtBetween(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<UserRewardEntity>

    fun countByRewardId(rewardId: Long): Int

    fun deleteByUserId(userId: Long)

}