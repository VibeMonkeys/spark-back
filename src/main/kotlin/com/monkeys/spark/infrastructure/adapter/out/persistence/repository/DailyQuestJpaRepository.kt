package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.DailyQuestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 일일 퀘스트 템플릿 JPA Repository
 * "삶을 게임처럼 즐겨라!" - 매일 반복되는 기본 퀘스트 템플릿 데이터 액세스
 */
@Repository
interface DailyQuestJpaRepository : JpaRepository<DailyQuestEntity, Long> {
    
    /**
     * 퀘스트 타입으로 조회
     */
    fun findByQuestType(questType: String): DailyQuestEntity?
    
    /**
     * 모든 활성 일일 퀘스트 조회 (순서대로 정렬)
     */
    @Query("SELECT dq FROM DailyQuestEntity dq WHERE dq.isActive = true ORDER BY dq.order ASC")
    fun findAllActiveQuestsOrderByOrder(): List<DailyQuestEntity>
    
    /**
     * 모든 일일 퀘스트 조회 (순서대로 정렬)
     */
    @Query("SELECT dq FROM DailyQuestEntity dq ORDER BY dq.order ASC")
    fun findAllOrderByOrder(): List<DailyQuestEntity>
    
    /**
     * 특정 순서의 퀘스트 조회
     */
    fun findByOrder(order: Int): DailyQuestEntity?
    
    /**
     * 활성 퀘스트 개수 조회
     */
    @Query("SELECT COUNT(dq) FROM DailyQuestEntity dq WHERE dq.isActive = true")
    fun countActiveQuests(): Long
    
    /**
     * 퀘스트 타입 존재 여부 확인
     */
    fun existsByQuestType(questType: String): Boolean
    
    /**
     * 활성 상태별 퀘스트 조회
     */
    fun findByIsActive(isActive: Boolean): List<DailyQuestEntity>
    
    /**
     * 특정 포인트 이상 보상하는 퀘스트 조회
     */
    @Query("SELECT dq FROM DailyQuestEntity dq WHERE dq.rewardPoints >= :minPoints ORDER BY dq.rewardPoints DESC")
    fun findByRewardPointsGreaterThanEqual(@Param("minPoints") minPoints: Int): List<DailyQuestEntity>
}