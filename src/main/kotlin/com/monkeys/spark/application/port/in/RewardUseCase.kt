package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.RewardCategory

/**
 * 리워드 관련 UseCase 인터페이스
 */
interface RewardUseCase {
    
    /**
     * 사용 가능한 리워드 목록 조회
     */
    fun getAvailableRewards(query: AvailableRewardsQuery): List<Reward>
    
    /**
     * 리워드 상세 조회
     */
    fun getReward(rewardId: RewardId): Reward?
    
    /**
     * 리워드 교환
     */
    fun exchangeReward(command: ExchangeRewardCommand): UserReward
    
    /**
     * 사용자 리워드 내역 조회
     */
    fun getUserRewards(query: UserRewardsQuery): List<UserReward>
    
    /**
     * 리워드 사용
     */
    fun useReward(command: UseRewardCommand): UserReward
    
    /**
     * 사용자 포인트 정보 조회
     */
    fun getUserPoints(userId: UserId): UserPointsSummary
    
    /**
     * 인기 리워드 조회
     */
    fun getPopularRewards(limit: Int): List<Reward>
    
    /**
     * 카테고리별 리워드 조회
     */
    fun getRewardsByCategory(category: RewardCategory): List<Reward>
    
    /**
     * 만료 임박 리워드 알림
     */
    fun getExpiringRewards(userId: UserId, withinDays: Int): List<UserReward>
    
    /**
     * 리워드 통계 조회
     */
    fun getRewardStatistics(userId: UserId): RewardStatistics
}