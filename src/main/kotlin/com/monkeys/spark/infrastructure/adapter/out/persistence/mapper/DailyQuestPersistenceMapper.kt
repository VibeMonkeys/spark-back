package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.DailyQuest
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.dailyquest.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.DailyQuestEntity
import org.springframework.stereotype.Component

/**
 * 일일 퀘스트 템플릿 Persistence Mapper
 * "삶을 게임처럼 즐겨라!" - 도메인 모델과 Entity 간 변환
 */
@Component
class DailyQuestPersistenceMapper {
    
    /**
     * 도메인 모델을 Entity로 변환
     */
    fun toEntity(domain: DailyQuest): DailyQuestEntity {
        val entity = DailyQuestEntity()
        entity.id = if (domain.id.value == "0") 0L else domain.id.value.toLongOrNull() ?: 0L
        entity.questType = domain.type.name
        entity.title = domain.title
        entity.description = domain.description
        entity.icon = domain.icon
        entity.order = domain.order
        entity.rewardPoints = domain.rewardPoints.value
        entity.statReward = domain.statReward.name
        entity.isActive = domain.isActive
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        return entity
    }
    
    /**
     * Entity를 도메인 모델로 변환
     */
    fun toDomain(entity: DailyQuestEntity): DailyQuest {
        return DailyQuest(
            id = DailyQuestId.from(entity.id.toString()),
            type = DailyQuestType.valueOf(entity.questType),
            title = entity.title,
            description = entity.description,
            icon = entity.icon,
            order = entity.order,
            rewardPoints = Points(entity.rewardPoints),
            statReward = DailyQuestStatType.valueOf(entity.statReward),
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * Entity 리스트를 도메인 모델 리스트로 변환
     */
    fun toDomainList(entities: List<DailyQuestEntity>): List<DailyQuest> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * 도메인 모델 리스트를 Entity 리스트로 변환
     */
    fun toEntityList(domains: List<DailyQuest>): List<DailyQuestEntity> {
        return domains.map { toEntity(it) }
    }
}