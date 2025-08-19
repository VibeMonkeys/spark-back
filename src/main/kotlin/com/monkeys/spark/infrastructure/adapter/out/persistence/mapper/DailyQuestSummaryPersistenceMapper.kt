package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.DailyQuestSummary
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.SpecialRewardTier
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.DailyQuestSummaryEntity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component

/**
 * 일일 퀘스트 요약 Persistence Mapper
 * "삶을 게임처럼 즐겨라!" - 도메인 모델과 Entity 간 변환
 */
@Component
class DailyQuestSummaryPersistenceMapper {
    
    private val objectMapper = jacksonObjectMapper()
    
    /**
     * 도메인 모델을 Entity로 변환
     */
    fun toEntity(domain: DailyQuestSummary): DailyQuestSummaryEntity {
        val entity = DailyQuestSummaryEntity()
        entity.userId = domain.userId.value.toLongOrNull() ?: 0L
        entity.summaryDate = domain.date
        entity.completedCount = domain.getCompletedCount()
        entity.totalCount = domain.getTotalCount()
        entity.completionPercentage = domain.getCompletionPercentage().value
        entity.specialRewardsEarned = serializeSpecialRewards(domain.getNewSpecialRewards())
        entity.baseRewardPoints = domain.getBaseRewardPoints().value
        entity.specialRewardPoints = domain.getSpecialRewardPoints().value
        entity.totalRewardPoints = domain.getTotalRewardPoints().value
        entity.totalStatReward = domain.getTotalStatReward()
        entity.statusMessage = domain.getStatusMessage()
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        return entity
    }
    
    /**
     * Entity를 도메인 모델로 변환
     */
    fun toDomain(entity: DailyQuestSummaryEntity): DailyQuestSummary {
        // Note: DailyQuestSummary는 복잡한 애그리게이트이므로 
        // 실제 구현에서는 DailyQuestProgress들도 함께 로드해야 함
        // 여기서는 기본 정보만 매핑하고, 실제 사용 시에는 
        // DailyQuestProgressRepository를 통해 progresses를 별도로 로드
        
        val specialRewards = deserializeSpecialRewards(entity.specialRewardsEarned)
        
        // 임시로 빈 progresses로 생성 (실제로는 별도 로드 필요)
        val summary = DailyQuestSummary(
            userId = UserId(entity.userId.toString()),
            date = entity.summaryDate,
            progresses = mutableListOf(),
            specialRewardsEarned = specialRewards.toMutableList(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
        
        return summary
    }
    
    /**
     * Entity 리스트를 도메인 모델 리스트로 변환
     */
    fun toDomainList(entities: List<DailyQuestSummaryEntity>): List<DailyQuestSummary> {
        return entities.map { toDomain(it) }
    }
    
    /**
     * 도메인 모델 리스트를 Entity 리스트로 변환
     */
    fun toEntityList(domains: List<DailyQuestSummary>): List<DailyQuestSummaryEntity> {
        return domains.map { toEntity(it) }
    }
    
    /**
     * 특수 보상 리스트를 JSON 문자열로 직렬화
     */
    private fun serializeSpecialRewards(rewards: List<SpecialRewardTier>): String {
        return try {
            objectMapper.writeValueAsString(rewards.map { it.name })
        } catch (e: Exception) {
            "[]"
        }
    }
    
    /**
     * JSON 문자열을 특수 보상 리스트로 역직렬화
     */
    private fun deserializeSpecialRewards(json: String): List<SpecialRewardTier> {
        return try {
            if (json.isBlank() || json == "[]") {
                emptyList()
            } else {
                val rewardNames: List<String> = objectMapper.readValue(json)
                rewardNames.mapNotNull { name ->
                    try {
                        SpecialRewardTier.valueOf(name)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}