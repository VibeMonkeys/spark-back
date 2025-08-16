package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserStatsRepository
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.UserStatsPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.UserStatsJpaRepository
import org.springframework.stereotype.Repository

/**
 * 사용자 스탯 영속성 어댑터
 */
@Repository
class UserStatsPersistenceAdapter(
    private val jpaRepository: UserStatsJpaRepository,
    private val mapper: UserStatsPersistenceMapper
) : UserStatsRepository {

    override fun findByUserId(userId: UserId): UserStats? {
        return jpaRepository.findByUserId(userId.value)?.let { mapper.toDomain(it) }
    }

    override fun save(userStats: UserStats): UserStats {
        val entity = mapper.toEntity(userStats)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
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
            .map { mapper.toDomain(it) }
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
        return entities.take(limit).map { mapper.toDomain(it) }
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
}