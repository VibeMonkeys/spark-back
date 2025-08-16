package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.dto.UserRankingInfo
import com.monkeys.spark.application.port.`in`.dto.UserStatsRankingItem
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType

/**
 * 사용자 스탯 유스케이스 인터페이스 (Inbound Port)
 */
interface UserStatsUseCase {
    
    /**
     * 사용자 스탯 조회
     */
    fun getUserStats(userId: UserId): UserStats

    /**
     * 스탯 포인트 할당
     */
    fun allocateStatPoints(userId: UserId, statType: StatType, points: Int): UserStats

    /**
     * 미션 완료로 스탯 증가
     */
    fun increaseMissionStat(userId: UserId, missionCategory: String): UserStats

    /**
     * 사용자 스탯 초기화 (신규 사용자)
     */
    fun initializeUserStats(userId: UserId): UserStats

    /**
     * 전체 스탯 랭킹 조회
     */
    fun getTotalStatsRanking(limit: Int = 100): List<UserStatsRankingItem>

    /**
     * 특정 스탯별 랭킹 조회
     */
    fun getStatRanking(statType: StatType, limit: Int = 100): List<UserStatsRankingItem>

    /**
     * 사용자 랭킹 정보 조회
     */
    fun getUserRankingInfo(userId: UserId): UserRankingInfo
}