package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.DailyQuestProgress
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.DailyQuestProgressEntity
import org.springframework.stereotype.Component

/**
 * 일일 퀘스트 진행 상황 Persistence Mapper
 * 도메인 모델과 Entity 간 변환
 */
@Component
class DailyQuestProgressPersistenceMapper {
    
    /**
     * 도메인 모델을 Entity로 변환
     */
    fun toEntity(domain: DailyQuestProgress): DailyQuestProgressEntity {
        val entity = DailyQuestProgressEntity()
        entity.id = if (domain.id.value == "0") 0L else domain.id.value.toLongOrNull() ?: 0L
        entity.userId = domain.userId.value.toLongOrNull() ?: 0L
        entity.dailyQuestId = domain.dailyQuestId.value.toLongOrNull() ?: 0L
        entity.questType = domain.questType.name
        entity.questDate = domain.date
        entity.isCompleted = domain.isCompleted
        entity.completedAt = domain.completedAt
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        return entity
    }
    
    /**
     * Entity를 도메인 모델로 변환
     */
    fun toDomain(entity: DailyQuestProgressEntity): DailyQuestProgress {
        return DailyQuestProgress(
            id = DailyQuestProgressId(entity.id.toString()),
            userId = UserId(entity.userId.toString()),
            dailyQuestId = DailyQuestId.from(entity.dailyQuestId.toString()),
            questType = DailyQuestType.valueOf(entity.questType),
            date = entity.questDate,
            isCompleted = entity.isCompleted,
            completedAt = entity.completedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * Entity 리스트를 도메인 모델 리스트로 변환
     */
    fun toDomainList(entities: List<DailyQuestProgressEntity>): List<DailyQuestProgress> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * 도메인 모델 리스트를 Entity 리스트로 변환
     */
    fun toEntityList(domains: List<DailyQuestProgress>): List<DailyQuestProgressEntity> {
        return domains.map { toEntity(it) }
    }
}