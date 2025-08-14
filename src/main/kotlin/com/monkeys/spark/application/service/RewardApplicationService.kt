package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.RewardUseCase
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.application.port.out.RewardRepository
import com.monkeys.spark.application.port.out.UserRewardRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.RewardCategory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RewardApplicationService(
    private val rewardRepository: RewardRepository,
    private val userRewardRepository: UserRewardRepository,
    private val userRepository: UserRepository
) : RewardUseCase {

    override fun getAvailableRewards(query: AvailableRewardsQuery): List<Reward> {
        // TODO: 실제 구현 필요 - 사용자 포인트에 따른 필터링
        return rewardRepository.findAllActive()
    }
    
    override fun getReward(rewardId: RewardId): Reward? {
        return rewardRepository.findById(rewardId)
    }
    
    override fun exchangeReward(command: ExchangeRewardCommand): UserReward {
        val userId = UserId(command.userId)
        val rewardId = RewardId(command.rewardId)
        
        // 리워드 조회
        val reward = rewardRepository.findById(rewardId)
            ?: throw IllegalArgumentException("Reward not found: ${command.rewardId}")
        
        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: ${command.userId}")
        
        // 포인트 부족 확인
        if (user.currentPoints.value < reward.requiredPoints.value) {
            throw IllegalArgumentException("포인트가 부족합니다. 필요 포인트: ${reward.requiredPoints.value}, 보유 포인트: ${user.currentPoints.value}")
        }
        
        // 리워드가 활성화되어 있는지 확인
        if (!reward.isActive) {
            throw IllegalArgumentException("교환할 수 없는 리워드입니다.")
        }
        
        // UserReward 생성
        val userReward = UserReward.create(
            userId = userId,
            reward = reward,
            pointsUsed = reward.requiredPoints
        )
        
        // 사용자 포인트 차감
        val updatedUser = user.spendPoints(reward.requiredPoints)
        userRepository.save(updatedUser)
        
        // UserReward 저장
        return userRewardRepository.save(userReward)
    }
    
    override fun getUserRewards(query: UserRewardsQuery): List<UserReward> {
        return userRewardRepository.findByUserId(UserId(query.userId))
    }
    
    override fun useReward(command: UseRewardCommand): UserReward {
        val userReward = userRewardRepository.findById(command.userRewardId)
            ?: throw IllegalArgumentException("UserReward not found: ${command.userRewardId}")
        
        // 사용자 권한 확인
        if (userReward.userId.value != command.userId) {
            throw IllegalArgumentException("권한이 없습니다.")
        }
        
        // 리워드 사용 처리
        val usedReward = userReward.use()
        return userRewardRepository.save(usedReward)
    }
    
    override fun getUserPoints(userId: UserId): UserPointsSummary {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")
        
        return UserPointsSummary(
            current = user.currentPoints.value,
            total = user.totalPoints.value,
            thisMonth = 0, // TODO: 이번 달 획득 포인트 계산
            spent = userRewardRepository.getTotalPointsSpentByUserId(userId).value,
            thisMonthSpent = userRewardRepository.getThisMonthPointsSpentByUserId(userId).value
        )
    }
    
    override fun getPopularRewards(limit: Int): List<Reward> {
        return rewardRepository.findPopularRewards()
    }
    
    override fun getRewardsByCategory(category: RewardCategory): List<Reward> {
        return rewardRepository.findByCategory(category)
    }
    
    override fun getExpiringRewards(userId: UserId, withinDays: Int): List<UserReward> {
        return userRewardRepository.findExpiringWithinDays(withinDays)
            .filter { userReward -> 
                // TODO: 사용자 필터링 로직 추가
                true
            }
    }
    
    override fun getRewardStatistics(userId: UserId): RewardStatistics {
        // TODO: 실제 구현 필요 - 리워드 통계 계산
        return RewardStatistics(
            totalExchanged = 0,
            totalPointsSpent = userRewardRepository.getTotalPointsSpentByUserId(userId).value,
            thisMonthExchanged = 0, // TODO: 이번 달 교환 수 계산
            thisMonthPointsSpent = userRewardRepository.getThisMonthPointsSpentByUserId(userId).value,
            favoriteCategory = "FOOD", // TODO: 실제 선호 카테고리 계산
            mostUsedBrand = "스타벅스" // TODO: 실제 최다 사용 브랜드 계산
        )
    }
}