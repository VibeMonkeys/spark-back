package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserStatsRepository
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType
import com.monkeys.spark.domain.vo.stat.StatValue
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserStatsEntity
import org.springframework.stereotype.Repository

/**
 * 사용자 스탯 리포지토리 구현체
 */
@Repository
class UserStatsRepositoryImpl(
    private val jpaRepository: UserStatsJpaRepository
) : UserStatsRepository {

    override fun findByUserId(userId: UserId): UserStats? {
        return jpaRepository.findByUserId(userId.value)?.toDomain()
    }

    override fun save(userStats: UserStats): UserStats {
        val entity = userStats.toEntity()
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun existsByUserId(userId: UserId): Boolean {
        return jpaRepository.existsByUserId(userId.value)
    }

    override fun deleteByUserId(userId: UserId) {
        jpaRepository.deleteById(userId.value)
    }

    override fun findRankingByTotalStats(limit: Int): List<UserStats> {
        return jpaRepository.findAllOrderByTotalStatsDesc()
            .take(limit)
            .map { it.toDomain() }
    }

    override fun findRankingByStat(statType: StatType, limit: Int): List<UserStats> {
        val entities = when (statType) {
            StatType.STRENGTH -> jpaRepository.findTopByStrength()
            StatType.INTELLIGENCE -> jpaRepository.findTopByIntelligence()
            StatType.CREATIVITY -> jpaRepository.findTopByCreativity()
            StatType.SOCIABILITY -> jpaRepository.findTopBySociability()
            StatType.ADVENTUROUS -> jpaRepository.findTopByAdventurous()
            StatType.DISCIPLINE -> jpaRepository.findTopByDiscipline()
        }
        return entities.take(limit).map { it.toDomain() }
    }

    override fun getUserRankByTotalStats(userId: UserId): Int? {
        val allStats = jpaRepository.findAllOrderByTotalStatsDesc()
        return allStats.indexOfFirst { it.userId == userId.value }.let { index ->
            if (index >= 0) index + 1 else null
        }
    }

    override fun getUserRankByStat(userId: UserId, statType: StatType): Int? {
        val allStats = when (statType) {
            StatType.STRENGTH -> jpaRepository.findTopByStrength()
            StatType.INTELLIGENCE -> jpaRepository.findTopByIntelligence()
            StatType.CREATIVITY -> jpaRepository.findTopByCreativity()
            StatType.SOCIABILITY -> jpaRepository.findTopBySociability()
            StatType.ADVENTUROUS -> jpaRepository.findTopByAdventurous()
            StatType.DISCIPLINE -> jpaRepository.findTopByDiscipline()
        }
        return allStats.indexOfFirst { it.userId == userId.value }.let { index ->
            if (index >= 0) index + 1 else null
        }
    }

    /**
     * 엔티티를 도메인으로 변환
     */
    private fun UserStatsEntity.toDomain(): UserStats {
        return UserStats.reconstitute(
            userId = UserId(this.userId),
            strength = StatValue(this.strengthCurrent, this.strengthAllocated),
            intelligence = StatValue(this.intelligenceCurrent, this.intelligenceAllocated),
            creativity = StatValue(this.creativityCurrent, this.creativityAllocated),
            sociability = StatValue(this.sociabilityCurrent, this.sociabilityAllocated),
            adventurous = StatValue(this.adventurousCurrent, this.adventurousAllocated),
            discipline = StatValue(this.disciplineCurrent, this.disciplineAllocated),
            availablePoints = this.availablePoints,
            totalEarnedPoints = this.totalEarnedPoints,
            lastUpdatedAt = this.lastUpdatedAt,
            createdAt = this.createdAt
        )
    }

    /**
     * 도메인을 엔티티로 변환
     */
    private fun UserStats.toEntity(): UserStatsEntity {
        return UserStatsEntity(
            userId = this.userId.value,
            strengthCurrent = this.strength.current,
            strengthAllocated = this.strength.allocated,
            intelligenceCurrent = this.intelligence.current,
            intelligenceAllocated = this.intelligence.allocated,
            creativityCurrent = this.creativity.current,
            creativityAllocated = this.creativity.allocated,
            sociabilityCurrent = this.sociability.current,
            sociabilityAllocated = this.sociability.allocated,
            adventurousCurrent = this.adventurous.current,
            adventurousAllocated = this.adventurous.allocated,
            disciplineCurrent = this.discipline.current,
            disciplineAllocated = this.discipline.allocated,
            availablePoints = this.availablePoints,
            totalEarnedPoints = this.totalEarnedPoints,
            lastUpdatedAt = this.lastUpdatedAt,
            createdAt = this.createdAt
        )
    }
}