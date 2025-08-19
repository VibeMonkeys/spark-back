package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.dailyquest.*
import java.time.LocalDateTime

/**
 * 일일 퀘스트 템플릿
 * "삶을 게임처럼 즐겨라!" - 매일 반복되는 생활 루틴을 게임화
 */
data class DailyQuest(
    val id: DailyQuestId,
    val type: DailyQuestType,
    val title: String,
    val description: String,
    val icon: String,
    val order: Int,
    val rewardPoints: Points = Points(5), // 기본 5포인트
    val statReward: DailyQuestStatType = DailyQuestStatType.DISCIPLINE,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        /**
         * 기본 일일 퀘스트 4개를 생성
         */
        fun createDefaultQuests(): List<DailyQuest> {
            return DailyQuestType.getAllQuests().map { type ->
                DailyQuest(
                    id = DailyQuestId.generate(),
                    type = type,
                    title = type.title,
                    description = type.description,
                    icon = type.icon,
                    order = type.order
                )
            }
        }
        
        /**
         * 특정 타입의 일일 퀘스트 생성
         */
        fun create(type: DailyQuestType): DailyQuest {
            return DailyQuest(
                id = DailyQuestId.generate(),
                type = type,
                title = type.title,
                description = type.description,
                icon = type.icon,
                order = type.order
            )
        }
    }
    
    /**
     * 퀘스트 완료 시 얻을 수 있는 총 보상 계산
     */
    fun getBaseReward(): DailyQuestReward {
        return DailyQuestReward(
            basePoints = rewardPoints,
            statBonus = 1
        )
    }
    
    /**
     * 퀘스트가 활성화되어 있는지 확인
     */
    fun canBeCompleted(): Boolean = isActive
}