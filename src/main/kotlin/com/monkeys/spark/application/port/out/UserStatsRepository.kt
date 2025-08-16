package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType

/**
 * 사용자 스탯 데이터 접근을 위한 포트 인터페이스
 */
interface UserStatsRepository {
    
    /**
     * 사용자 ID로 스탯 정보 조회
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
     * 전체 스탯 기준 랭킹 조회
     */
    fun findRankingByTotalStats(limit: Int): List<UserStats>
    
    /**
     * 특정 스탯 기준 랭킹 조회
     */
    fun findRankingByStat(statType: StatType, limit: Int): List<UserStats>
    
    /**
     * 사용자의 전체 스탯 순위 조회
     */
    fun getUserRankByTotalStats(userId: UserId): Int?
    
    /**
     * 사용자의 특정 스탯 순위 조회
     */
    fun getUserRankByStat(userId: UserId, statType: StatType): Int?
}