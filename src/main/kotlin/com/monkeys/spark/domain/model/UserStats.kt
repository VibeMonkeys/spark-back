package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType
import com.monkeys.spark.domain.vo.stat.StatValue
import java.time.LocalDateTime

/**
 * 사용자 스탯 도메인 모델
 * 사용자의 RPG 스타일 스탯 정보를 관리
 */
data class UserStats(
    val userId: UserId,
    val strength: StatValue,
    val intelligence: StatValue,
    val creativity: StatValue,
    val sociability: StatValue,
    val adventurous: StatValue,
    val discipline: StatValue,
    val availablePoints: Int,
    val totalEarnedPoints: Int,
    val lastUpdatedAt: LocalDateTime,
    val createdAt: LocalDateTime
) {

    /**
     * 전체 스탯 총합
     */
    val totalStats: Int get() = 
        strength.current + intelligence.current + creativity.current + 
        sociability.current + adventurous.current + discipline.current

    /**
     * 평균 스탯 값
     */
    val averageStatValue: Double get() = totalStats / 6.0
    
    /**
     * 총 포인트 (업적 시스템용)
     */
    val totalPoints: Int get() = totalEarnedPoints
    
    /**
     * 완료한 미션 수 (업적 시스템용 - 임시)
     */
    val completedMissions: Int get() = totalEarnedPoints / 3 // 미션당 평균 3포인트로 추정
    
    /**
     * 현재 연속 달성일 (업적 시스템용 - 임시)
     */
    val currentStreak: Int get() = if (totalEarnedPoints > 10) 5 else 1 // 임시 구현

    /**
     * 특정 스탯 값 조회
     */
    fun getStatValue(statType: StatType): StatValue = when (statType) {
        StatType.STRENGTH -> strength
        StatType.INTELLIGENCE -> intelligence
        StatType.CREATIVITY -> creativity
        StatType.SOCIABILITY -> sociability
        StatType.ADVENTUROUS -> adventurous
        StatType.DISCIPLINE -> discipline
    }

    /**
     * 스탯 포인트 할당
     */
    fun allocateStatPoints(statType: StatType, points: Int): UserStats {
        require(points > 0) { "할당할 포인트는 0보다 커야 합니다" }
        require(points <= availablePoints) { "사용 가능한 포인트가 부족합니다" }

        val updatedStat = getStatValue(statType).allocatePoints(points)
        
        return when (statType) {
            StatType.STRENGTH -> copy(
                strength = updatedStat,
                availablePoints = availablePoints - points,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.INTELLIGENCE -> copy(
                intelligence = updatedStat,
                availablePoints = availablePoints - points,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.CREATIVITY -> copy(
                creativity = updatedStat,
                availablePoints = availablePoints - points,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.SOCIABILITY -> copy(
                sociability = updatedStat,
                availablePoints = availablePoints - points,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.ADVENTUROUS -> copy(
                adventurous = updatedStat,
                availablePoints = availablePoints - points,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.DISCIPLINE -> copy(
                discipline = updatedStat,
                availablePoints = availablePoints - points,
                lastUpdatedAt = LocalDateTime.now()
            )
        }
    }

    /**
     * 미션 완료로 스탯 증가
     */
    fun increaseMissionStat(missionCategory: String): UserStats {
        val statType = StatType.fromMissionCategory(missionCategory) ?: StatType.DISCIPLINE
        
        return when (statType) {
            StatType.STRENGTH -> copy(
                strength = strength.increaseByStat(MISSION_COMPLETION_STAT_POINTS),
                availablePoints = availablePoints + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                totalEarnedPoints = totalEarnedPoints + MISSION_COMPLETION_STAT_POINTS + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.INTELLIGENCE -> copy(
                intelligence = intelligence.increaseByStat(MISSION_COMPLETION_STAT_POINTS),
                availablePoints = availablePoints + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                totalEarnedPoints = totalEarnedPoints + MISSION_COMPLETION_STAT_POINTS + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.CREATIVITY -> copy(
                creativity = creativity.increaseByStat(MISSION_COMPLETION_STAT_POINTS),
                availablePoints = availablePoints + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                totalEarnedPoints = totalEarnedPoints + MISSION_COMPLETION_STAT_POINTS + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.SOCIABILITY -> copy(
                sociability = sociability.increaseByStat(MISSION_COMPLETION_STAT_POINTS),
                availablePoints = availablePoints + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                totalEarnedPoints = totalEarnedPoints + MISSION_COMPLETION_STAT_POINTS + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.ADVENTUROUS -> copy(
                adventurous = adventurous.increaseByStat(MISSION_COMPLETION_STAT_POINTS),
                availablePoints = availablePoints + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                totalEarnedPoints = totalEarnedPoints + MISSION_COMPLETION_STAT_POINTS + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                lastUpdatedAt = LocalDateTime.now()
            )
            StatType.DISCIPLINE -> copy(
                discipline = discipline.increaseByStat(MISSION_COMPLETION_STAT_POINTS),
                availablePoints = availablePoints + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                totalEarnedPoints = totalEarnedPoints + MISSION_COMPLETION_STAT_POINTS + MISSION_COMPLETION_ALLOCATABLE_POINTS,
                lastUpdatedAt = LocalDateTime.now()
            )
        }
    }

    /**
     * 최고 스탯 조회
     */
    val dominantStat: Pair<StatType, StatValue> get() {
        val stats = mapOf(
            StatType.STRENGTH to strength,
            StatType.INTELLIGENCE to intelligence,
            StatType.CREATIVITY to creativity,
            StatType.SOCIABILITY to sociability,
            StatType.ADVENTUROUS to adventurous,
            StatType.DISCIPLINE to discipline
        )
        return stats.maxByOrNull { it.value.current }!!.toPair()
    }

    companion object {
        /**
         * 초기 사용자 스탯 생성
         */
        fun createInitial(userId: UserId): UserStats {
            val now = LocalDateTime.now()
            return UserStats(
                userId = userId,
                strength = StatValue(INITIAL_STAT, 0),
                intelligence = StatValue(INITIAL_STAT, 0),
                creativity = StatValue(INITIAL_STAT, 0),
                sociability = StatValue(INITIAL_STAT, 0),
                adventurous = StatValue(INITIAL_STAT, 0),
                discipline = StatValue(INITIAL_STAT, 0),
                availablePoints = 0,
                totalEarnedPoints = 0,
                lastUpdatedAt = now,
                createdAt = now
            )
        }

        /**
         * 기존 데이터로부터 재구성
         */
        fun reconstitute(
            userId: UserId,
            strength: StatValue,
            intelligence: StatValue,
            creativity: StatValue,
            sociability: StatValue,
            adventurous: StatValue,
            discipline: StatValue,
            availablePoints: Int,
            totalEarnedPoints: Int,
            lastUpdatedAt: LocalDateTime,
            createdAt: LocalDateTime
        ): UserStats = UserStats(
            userId, strength, intelligence, creativity, sociability, adventurous, discipline,
            availablePoints, totalEarnedPoints, lastUpdatedAt, createdAt
        )

        const val INITIAL_STAT = 10
        const val MISSION_COMPLETION_STAT_POINTS = 1 // 미션 완료시 받는 스탯 포인트 (자동 증가)
        const val MISSION_COMPLETION_ALLOCATABLE_POINTS = 2 // 미션 완료시 받는 할당 가능 포인트 (수동 할당)

    }
}