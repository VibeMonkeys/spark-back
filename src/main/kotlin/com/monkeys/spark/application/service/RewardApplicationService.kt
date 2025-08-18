package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.RewardUseCase
import com.monkeys.spark.application.port.`in`.command.ExchangeRewardCommand
import com.monkeys.spark.application.port.`in`.command.UseRewardCommand
import com.monkeys.spark.application.port.`in`.query.AvailableRewardsQuery
import com.monkeys.spark.application.port.`in`.query.UserRewardsQuery
import com.monkeys.spark.application.port.`in`.query.UserPointsSummary
import com.monkeys.spark.application.port.`in`.query.RewardStatistics
import com.monkeys.spark.application.port.out.RewardRepository
import com.monkeys.spark.application.port.out.UserRewardRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.RewardCategory
import com.monkeys.spark.domain.service.RewardDomainService
import com.monkeys.spark.domain.exception.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RewardApplicationService(
    private val rewardRepository: RewardRepository,
    private val userRewardRepository: UserRewardRepository,
    private val userRepository: UserRepository,
    private val rewardDomainService: RewardDomainService
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
            ?: throw RewardNotFoundException(command.rewardId.toString())
        
        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())
        
        // 도메인 서비스를 통한 비즈니스 규칙 검증
        if (!rewardDomainService.canExchangeReward(user, reward)) {
            if (!reward.isActive) {
                throw RewardNotActiveException(command.rewardId.toString())
            }
            if (user.currentPoints.value < reward.requiredPoints.value) {
                throw InsufficientPointsException(reward.requiredPoints.value, user.currentPoints.value)
            }
        }
        
        // 도메인 서비스를 통한 리워드 교환
        val (updatedUser, userReward) = rewardDomainService.exchangeReward(user, reward)
        
        // 변경사항 저장
        userRepository.save(updatedUser)
        return userRewardRepository.save(userReward)
    }
    
    override fun getUserRewards(query: UserRewardsQuery): List<UserReward> {
        return userRewardRepository.findByUserId(UserId(query.userId))
    }
    
    override fun useReward(command: UseRewardCommand): UserReward {
        val userReward = userRewardRepository.findById(UserRewardId(command.userRewardId))
            ?: throw UserRewardNotFoundException(command.userRewardId.toString())
        
        // 도메인 서비스를 통한 권한 확인
        if (!rewardDomainService.canUseUserReward(userReward, command.userId.toString())) {
            throw UnauthorizedRewardAccessException(command.userId.toString())
        }
        
        // 리워드 사용 처리
        val usedReward = userReward.use()
        return userRewardRepository.save(usedReward)
    }
    
    override fun getUserPoints(userId: UserId): UserPointsSummary {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())
        
        val thisMonthEarned = userRepository.getThisMonthEarnedPoints(userId)
        
        return UserPointsSummary(
            current = user.currentPoints.value,
            total = user.totalPoints.value,
            thisMonth = thisMonthEarned.value,
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
            .filter { _ -> 
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