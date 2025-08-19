package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.DailyQuestRepository
import com.monkeys.spark.domain.model.DailyQuest
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.DailyQuestPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.DailyQuestJpaRepository
import org.springframework.stereotype.Repository

/**
 * 일일 퀘스트 템플릿 Persistence Adapter
 * "삶을 게임처럼 즐겨라!" - 매일 반복되는 기본 퀘스트 템플릿 데이터 액세스 구현
 */
@Repository
class DailyQuestPersistenceAdapter(
    private val jpaRepository: DailyQuestJpaRepository,
    private val mapper: DailyQuestPersistenceMapper
) : DailyQuestRepository {
    
    override fun save(dailyQuest: DailyQuest): DailyQuest {
        val entity = mapper.toEntity(dailyQuest)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }
    
    override fun findById(dailyQuestId: DailyQuestId): DailyQuest? {
        val id = dailyQuestId.value.toLongOrNull() ?: return null
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun findByType(questType: DailyQuestType): DailyQuest? {
        return jpaRepository.findByQuestType(questType.name)
            ?.let { mapper.toDomain(it) }
    }
    
    override fun findAllActiveQuests(): List<DailyQuest> {
        return jpaRepository.findAllActiveQuestsOrderByOrder()
            .let { mapper.toDomainList(it) }
    }
    
    override fun findAll(): List<DailyQuest> {
        return jpaRepository.findAllOrderByOrder()
            .let { mapper.toDomainList(it) }
    }
    
    override fun findByOrder(order: Int): DailyQuest? {
        return jpaRepository.findByOrder(order)
            ?.let { mapper.toDomain(it) }
    }
    
    override fun updateActiveStatus(dailyQuestId: DailyQuestId, isActive: Boolean): DailyQuest? {
        val id = dailyQuestId.value.toLongOrNull() ?: return null
        return jpaRepository.findById(id)
            .map { entity ->
                entity.isActive = isActive
                entity.updatedAt = java.time.LocalDateTime.now()
                val savedEntity = jpaRepository.save(entity)
                mapper.toDomain(savedEntity)
            }
            .orElse(null)
    }
    
    override fun deleteById(dailyQuestId: DailyQuestId) {
        val id = dailyQuestId.value.toLongOrNull() ?: return
        jpaRepository.deleteById(id)
    }
    
    override fun countActiveQuests(): Long {
        return jpaRepository.countActiveQuests()
    }
    
    override fun existsById(dailyQuestId: DailyQuestId): Boolean {
        val id = dailyQuestId.value.toLongOrNull() ?: return false
        return jpaRepository.existsById(id)
    }
    
    override fun existsByType(questType: DailyQuestType): Boolean {
        return jpaRepository.existsByQuestType(questType.name)
    }
}