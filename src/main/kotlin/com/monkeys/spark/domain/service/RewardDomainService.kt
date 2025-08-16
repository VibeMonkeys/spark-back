package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.model.UserReward

/**
 * 리워드 관련 순수 도메인 서비스
 */
class RewardDomainService {
    
    /**
     * 사용자가 리워드를 교환할 수 있는지 검증
     */
    fun canExchangeReward(user: User, reward: Reward): Boolean {
        // 리워드가 활성화되어 있는지 확인
        if (!reward.isActive) {
            return false
        }
        
        // 사용자가 충분한 포인트를 가지고 있는지 확인
        if (user.currentPoints.value < reward.requiredPoints.value) {
            return false
        }
        
        return true
    }
    
    /**
     * 리워드 교환 처리
     */
    fun exchangeReward(user: User, reward: Reward): Pair<User, UserReward> {
        require(canExchangeReward(user, reward)) {
            "Cannot exchange reward: insufficient points or reward not active"
        }
        
        // UserReward 생성
        val userReward = UserReward.create(
            userId = user.id,
            reward = reward,
            pointsUsed = reward.requiredPoints
        )
        
        // 사용자 포인트 차감
        val updatedUser = user.spendPoints(reward.requiredPoints)
        
        return Pair(updatedUser, userReward)
    }
    
    /**
     * 사용자가 해당 UserReward를 사용할 수 있는지 검증
     */
    fun canUseUserReward(userReward: UserReward, userId: String): Boolean {
        return userReward.userId.value == userId
    }
}