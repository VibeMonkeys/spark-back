package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatValue
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserStatsEntity
import org.springframework.stereotype.Component

/**
 * 사용자 스탯 도메인 <-> 엔티티 매퍼
 */
@Component
class UserStatsPersistenceMapper {
    
    /**
     * 엔티티를 도메인으로 변환
     */
    fun toDomain(entity: UserStatsEntity): UserStats {
        return UserStats.reconstitute(
            userId = UserId(entity.userId),
            strength = StatValue(entity.strengthCurrent, entity.strengthAllocated),
            intelligence = StatValue(entity.intelligenceCurrent, entity.intelligenceAllocated),
            creativity = StatValue(entity.creativityCurrent, entity.creativityAllocated),
            sociability = StatValue(entity.sociabilityCurrent, entity.sociabilityAllocated),
            adventurous = StatValue(entity.adventurousCurrent, entity.adventurousAllocated),
            discipline = StatValue(entity.disciplineCurrent, entity.disciplineAllocated),
            availablePoints = entity.availablePoints,
            totalEarnedPoints = entity.totalEarnedPoints,
            lastUpdatedAt = entity.lastUpdatedAt,
            createdAt = entity.createdAt
        )
    }
    
    /**
     * 도메인을 엔티티로 변환
     */
    fun toEntity(domain: UserStats): UserStatsEntity {
        return UserStatsEntity(
            userId = domain.userId.value,
            strengthCurrent = domain.strength.current,
            strengthAllocated = domain.strength.allocated,
            intelligenceCurrent = domain.intelligence.current,
            intelligenceAllocated = domain.intelligence.allocated,
            creativityCurrent = domain.creativity.current,
            creativityAllocated = domain.creativity.allocated,
            sociabilityCurrent = domain.sociability.current,
            sociabilityAllocated = domain.sociability.allocated,
            adventurousCurrent = domain.adventurous.current,
            adventurousAllocated = domain.adventurous.allocated,
            disciplineCurrent = domain.discipline.current,
            disciplineAllocated = domain.discipline.allocated,
            availablePoints = domain.availablePoints,
            totalEarnedPoints = domain.totalEarnedPoints,
            lastUpdatedAt = domain.lastUpdatedAt,
            createdAt = domain.createdAt
        )
    }
}