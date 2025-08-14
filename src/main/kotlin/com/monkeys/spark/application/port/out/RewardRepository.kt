package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.RewardId
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.reward.RewardCategory
import com.monkeys.spark.domain.vo.reward.BrandName
import java.time.LocalDateTime

interface RewardRepository {
    
    /**
     * 리워드 저장 (생성 및 수정)
     */
    fun save(reward: Reward): Reward
    
    /**
     * 리워드 ID로 조회
     */
    fun findById(rewardId: RewardId): Reward?
    
    /**
     * 모든 활성 리워드 조회
     */
    fun findAllActive(): List<Reward>
    
    /**
     * 카테고리별 리워드 조회
     */
    fun findByCategory(category: RewardCategory): List<Reward>
    
    /**
     * 인기 리워드 조회
     */
    fun findPopularRewards(): List<Reward>
    
    /**
     * 프리미엄 리워드 조회
     */
    fun findPremiumRewards(): List<Reward>
    
    /**
     * 포인트 범위로 리워드 조회
     */
    fun findByPointsRange(minPoints: Points, maxPoints: Points): List<Reward>
    
    /**
     * 브랜드별 리워드 조회
     */
    fun findByBrand(brand: BrandName): List<Reward>
    
    /**
     * 리워드 교환 기록
     */
    fun recordExchange(rewardId: RewardId): Reward?
    
    /**
     * 리워드 삭제
     */
    fun deleteById(rewardId: RewardId)
    
    /**
     * 리워드 활성화/비활성화
     */
    fun updateActiveStatus(rewardId: RewardId, isActive: Boolean): Reward?
}