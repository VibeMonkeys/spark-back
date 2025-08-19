package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 사용자별 일일 퀘스트 진행 상황
 * 특정 날짜의 개별 퀘스트 완료 여부를 추적
 */
data class DailyQuestProgress(
    val id: DailyQuestProgressId,
    val userId: UserId,
    val dailyQuestId: DailyQuestId,
    val questType: DailyQuestType,
    val date: LocalDate,
    var isCompleted: Boolean = false,
    var completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        /**
         * 새로운 일일 퀘스트 진행 상황 생성
         */
        fun create(
            userId: UserId,
            dailyQuest: DailyQuest,
            date: LocalDate = LocalDate.now()
        ): DailyQuestProgress {
            return DailyQuestProgress(
                id = DailyQuestProgressId.generate(),
                userId = userId,
                dailyQuestId = dailyQuest.id,
                questType = dailyQuest.type,
                date = date
            )
        }
        
        /**
         * 사용자의 특정 날짜 모든 퀘스트 진행 상황 생성
         */
        fun createForAllQuests(
            userId: UserId,
            dailyQuests: List<DailyQuest>,
            date: LocalDate = LocalDate.now()
        ): List<DailyQuestProgress> {
            return dailyQuests.map { quest ->
                create(userId, quest, date)
            }
        }
    }
    
    /**
     * 퀘스트 완료 처리
     */
    fun complete(): DailyQuestProgress {
        require(!isCompleted) { "Daily quest already completed" }
        require(date <= LocalDate.now()) { "Cannot complete future daily quest" }
        
        isCompleted = true
        completedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 퀘스트 완료 취소 (같은 날 내에서만 가능)
     */
    fun uncomplete(): DailyQuestProgress {
        require(isCompleted) { "Daily quest is not completed" }
        require(date == LocalDate.now()) { "Can only uncomplete today's quest" }
        
        isCompleted = false
        completedAt = null
        updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 오늘의 퀘스트인지 확인
     */
    fun isToday(): Boolean = date == LocalDate.now()
    
    /**
     * 완료 가능한 상태인지 확인
     */
    fun canBeCompleted(): Boolean = !isCompleted && date <= LocalDate.now()
    
    /**
     * 완료 취소 가능한 상태인지 확인
     */
    fun canBeUncompleted(): Boolean = isCompleted && isToday()
}