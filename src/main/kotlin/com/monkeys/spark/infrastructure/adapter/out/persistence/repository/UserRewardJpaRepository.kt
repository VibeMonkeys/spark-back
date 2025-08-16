package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserRewardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRewardJpaRepository : JpaRepository<UserRewardEntity, String> {

    fun findByUserId(userId: String): List<UserRewardEntity>

    fun findByUserIdAndStatus(userId: String, status: String): List<UserRewardEntity>

    fun findByExchangeCode(exchangeCode: String): UserRewardEntity?

    @Query("SELECT ur FROM UserRewardEntity ur WHERE ur.userId = :userId ORDER BY ur.createdAt DESC")
    fun findUserRewardsByCreatedDesc(@Param("userId") userId: String): List<UserRewardEntity>

    @Query("SELECT ur FROM UserRewardEntity ur WHERE ur.expiresAt < :currentTime AND ur.status = 'AVAILABLE'")
    fun findExpiredRewards(@Param("currentTime") currentTime: LocalDateTime): List<UserRewardEntity>

    @Query("SELECT ur FROM UserRewardEntity ur WHERE ur.userId = :userId AND ur.status = 'AVAILABLE' AND ur.expiresAt > :currentTime")
    fun findAvailableRewardsByUser(
        @Param("userId") userId: String,
        @Param("currentTime") currentTime: LocalDateTime
    ): List<UserRewardEntity>

    @Query("SELECT COUNT(ur) FROM UserRewardEntity ur WHERE ur.rewardId = :rewardId AND ur.status = 'USED'")
    fun countUsedRewardsByRewardId(@Param("rewardId") rewardId: String): Long

    @Query("SELECT SUM(ur.pointsUsed) FROM UserRewardEntity ur WHERE ur.userId = :userId")
    fun sumPointsUsedByUser(@Param("userId") userId: String): Int?

    // UserRewardPersistenceAdapter에서 필요한 추가 메서드들
    fun findByStatusAndExpiresAtBefore(status: String, expirationDate: LocalDateTime): List<UserRewardEntity>

    fun findByUserIdAndExchangedAtBetween(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<UserRewardEntity>

    fun countByRewardId(rewardId: String): Int

    fun deleteByUserId(userId: String)
}