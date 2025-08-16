package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.RewardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RewardJpaRepository : JpaRepository<RewardEntity, String> {

    @Query("SELECT r FROM RewardEntity r WHERE r.isActive = true ORDER BY r.isPopular DESC, r.requiredPoints ASC")
    fun findActiveRewardsOrderByPopularityAndPoints(): List<RewardEntity>

    @Query("SELECT r FROM RewardEntity r WHERE r.category = :category AND r.isActive = true ORDER BY r.requiredPoints ASC")
    fun findActiveRewardsByCategory(@Param("category") category: String): List<RewardEntity>

    @Query("SELECT r FROM RewardEntity r WHERE r.isPopular = true AND r.isActive = true ORDER BY r.totalExchanged DESC")
    fun findPopularRewards(): List<RewardEntity>

    @Query("SELECT r FROM RewardEntity r WHERE r.isPremium = true AND r.isActive = true")
    fun findPremiumRewards(): List<RewardEntity>

    @Query("SELECT r FROM RewardEntity r WHERE r.requiredPoints <= :maxPoints AND r.isActive = true ORDER BY r.requiredPoints DESC")
    fun findAffordableRewards(@Param("maxPoints") maxPoints: Int): List<RewardEntity>

    @Query("SELECT r FROM RewardEntity r WHERE r.brand = :brand AND r.isActive = true")
    fun findRewardsByBrand(@Param("brand") brand: String): List<RewardEntity>

    @Query("SELECT COUNT(r) FROM RewardEntity r WHERE r.isActive = true")
    fun countActiveRewards(): Long

    // RewardPersistenceAdapter에서 필요한 추가 메서드들
    fun findByRequiredPointsBetween(minPoints: Int, maxPoints: Int): List<RewardEntity>

    fun findByBrand(brand: String): List<RewardEntity>
}