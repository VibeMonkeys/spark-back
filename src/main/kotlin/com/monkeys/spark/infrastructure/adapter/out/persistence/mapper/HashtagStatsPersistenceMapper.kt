package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.HashtagStats
import com.monkeys.spark.domain.vo.common.HashtagStatsId
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.HashtagStatsEntity
import org.springframework.stereotype.Component

/**
 * 해시태그 통계 도메인 ↔ 엔티티 매핑
 */
@Component
class HashtagStatsPersistenceMapper {
    
    /**
     * 도메인 모델을 JPA 엔티티로 변환
     */
    fun toEntity(domain: HashtagStats): HashtagStatsEntity {
        return HashtagStatsEntity().apply {
            id = domain.id.value
            hashtag = domain.hashtag.value
            dailyCount = domain.dailyCount
            weeklyCount = domain.weeklyCount
            monthlyCount = domain.monthlyCount
            totalCount = domain.totalCount
            lastUsedAt = domain.lastUsedAt
            date = domain.date
            trendScore = domain.trendScore
            createdAt = domain.createdAt
            updatedAt = domain.updatedAt
        }
    }
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    fun toDomain(entity: HashtagStatsEntity): HashtagStats {
        return HashtagStats(
            id = HashtagStatsId(entity.id),
            hashtag = HashTag(entity.hashtag),
            dailyCount = entity.dailyCount,
            weeklyCount = entity.weeklyCount,
            monthlyCount = entity.monthlyCount,
            totalCount = entity.totalCount,
            lastUsedAt = entity.lastUsedAt,
            date = entity.date,
            trendScore = entity.trendScore,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * 엔티티 리스트를 도메인 모델 리스트로 변환
     */
    fun toDomainList(entities: List<HashtagStatsEntity>): List<HashtagStats> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * 도메인 모델 리스트를 엔티티 리스트로 변환
     */
    fun toEntityList(domains: List<HashtagStats>): List<HashtagStatsEntity> {
        return domains.map { toEntity(it) }
    }
}