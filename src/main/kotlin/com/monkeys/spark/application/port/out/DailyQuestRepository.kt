package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.DailyQuest
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType

/**
 * 일일 퀘스트 템플릿 Repository (Outbound Port)
 * "삶을 게임처럼 즐겨라!" - 매일 반복되는 기본 퀘스트 템플릿 관리
 */
interface DailyQuestRepository {
    
    /**
     * 일일 퀘스트 저장 (생성 및 수정)
     */
    fun save(dailyQuest: DailyQuest): DailyQuest
    
    /**
     * 일일 퀘스트 ID로 조회
     */
    fun findById(dailyQuestId: DailyQuestId): DailyQuest?
    
    /**
     * 퀘스트 타입으로 조회
     */
    fun findByType(questType: DailyQuestType): DailyQuest?
    
    /**
     * 모든 활성 일일 퀘스트 조회 (순서대로 정렬)
     */
    fun findAllActiveQuests(): List<DailyQuest>
    
    /**
     * 모든 일일 퀘스트 조회 (활성/비활성 포함)
     */
    fun findAll(): List<DailyQuest>
    
    /**
     * 특정 순서의 퀘스트 조회
     */
    fun findByOrder(order: Int): DailyQuest?
    
    /**
     * 퀘스트 활성화 상태 변경
     */
    fun updateActiveStatus(dailyQuestId: DailyQuestId, isActive: Boolean): DailyQuest?
    
    /**
     * 퀘스트 삭제
     */
    fun deleteById(dailyQuestId: DailyQuestId)
    
    /**
     * 활성 퀘스트 개수 조회
     */
    fun countActiveQuests(): Long
    
    /**
     * 퀘스트 존재 여부 확인
     */
    fun existsById(dailyQuestId: DailyQuestId): Boolean
    
    /**
     * 퀘스트 타입 존재 여부 확인
     */
    fun existsByType(questType: DailyQuestType): Boolean
}