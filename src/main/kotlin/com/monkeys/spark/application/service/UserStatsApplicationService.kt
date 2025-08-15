package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.UserStatsUseCase
import com.monkeys.spark.application.port.`in`.UserStatsRankingItem
import com.monkeys.spark.application.port.`in`.UserRankingInfo
import com.monkeys.spark.application.port.out.UserStatsRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 스탯 애플리케이션 서비스
 */
@Service
@Transactional
class UserStatsApplicationService(
    private val userStatsRepository: UserStatsRepository,
    private val userRepository: UserRepository
) : UserStatsUseCase {

    override fun getUserStats(userId: UserId): UserStats {
        return userStatsRepository.findByUserId(userId)
            ?: initializeUserStats(userId)
    }

    override fun allocateStatPoints(userId: UserId, statType: StatType, points: Int): UserStats {
        val userStats = getUserStats(userId)
        val updatedStats = userStats.allocateStatPoints(statType, points)
        return userStatsRepository.save(updatedStats)
    }

    override fun increaseMissionStat(userId: UserId, missionCategory: String): UserStats {
        val userStats = getUserStats(userId)
        val updatedStats = userStats.increaseMissionStat(missionCategory)
        return userStatsRepository.save(updatedStats)
    }

    override fun initializeUserStats(userId: UserId): UserStats {
        // 사용자가 존재하는지 확인
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다: ${userId.value}")

        val initialStats = UserStats.createInitial(userId)
        return userStatsRepository.save(initialStats)
    }

    @Transactional(readOnly = true)
    override fun getTotalStatsRanking(limit: Int): List<UserStatsRankingItem> {
        val rankings = userStatsRepository.findRankingByTotalStats(limit)
        
        return rankings.mapIndexed { index, userStats ->
            val user = userRepository.findById(userStats.userId)
            UserStatsRankingItem(
                rank = index + 1,
                userId = userStats.userId.value,
                username = user?.name?.value ?: "Unknown",
                avatarUrl = user?.avatarUrl?.value,
                statValue = userStats.totalStats,
                totalStats = userStats.totalStats
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getStatRanking(statType: StatType, limit: Int): List<UserStatsRankingItem> {
        val rankings = userStatsRepository.findRankingByStat(statType, limit)
        
        return rankings.mapIndexed { index, userStats ->
            val user = userRepository.findById(userStats.userId)
            UserStatsRankingItem(
                rank = index + 1,
                userId = userStats.userId.value,
                username = user?.name?.value ?: "Unknown",
                avatarUrl = user?.avatarUrl?.value,
                statValue = userStats.getStatValue(statType).current,
                statType = statType,
                totalStats = userStats.totalStats
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getUserRankingInfo(userId: UserId): UserRankingInfo {
        return UserRankingInfo(
            userId = userId.value,
            totalStatsRank = userStatsRepository.getUserRankByTotalStats(userId) ?: 0,
            strengthRank = userStatsRepository.getUserRankByStat(userId, StatType.STRENGTH) ?: 0,
            intelligenceRank = userStatsRepository.getUserRankByStat(userId, StatType.INTELLIGENCE) ?: 0,
            creativityRank = userStatsRepository.getUserRankByStat(userId, StatType.CREATIVITY) ?: 0,
            sociabilityRank = userStatsRepository.getUserRankByStat(userId, StatType.SOCIABILITY) ?: 0,
            adventurousRank = userStatsRepository.getUserRankByStat(userId, StatType.ADVENTUROUS) ?: 0,
            disciplineRank = userStatsRepository.getUserRankByStat(userId, StatType.DISCIPLINE) ?: 0,
            totalUsers = 1000 // TODO: 실제 사용자 수 조회
        )
    }
}