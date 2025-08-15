package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType

/**
 * 사용자 스탯 리포지토리 인터페이스 (Outbound Port)
 */
interface UserStatsRepository {
    
    /**
     * 사용자 스탯 조회
     */
    fun findByUserId(userId: UserId): UserStats?

    /**
     * 사용자 스탯 저장
     */
    fun save(userStats: UserStats): UserStats

    /**
     * 사용자 스탯 존재 여부 확인
     */
    fun existsByUserId(userId: UserId): Boolean

    /**
     * 사용자 스탯 삭제
     */
    fun deleteByUserId(userId: UserId)

    /**
     * 전체 스탯 순 랭킹 조회
     */
    fun findRankingByTotalStats(limit: Int = 100): List<UserStats>

    /**
     * 특정 스탯별 랭킹 조회
     */
    fun findRankingByStat(statType: StatType, limit: Int = 100): List<UserStats>

    /**
     * 사용자의 전체 랭킹 조회
     */
    fun getUserRankByTotalStats(userId: UserId): Int?

    /**
     * 사용자의 특정 스탯 랭킹 조회
     */
    fun getUserRankByStat(userId: UserId, statType: StatType): Int?
}