package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 일일 퀘스트 요약 (Aggregate Root)
 * "삶을 게임처럼 즐겨라!" - 하루의 퀘스트 전체 현황과 보상 관리
 */
data class DailyQuestSummary(
    val userId: UserId,
    val date: LocalDate,
    val progresses: MutableList<DailyQuestProgress>,
    var specialRewardsEarned: MutableList<SpecialRewardTier> = mutableListOf(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        /**
         * 새로운 일일 퀘스트 요약 생성
         */
        fun create(
            userId: UserId,
            dailyQuests: List<DailyQuest>,
            date: LocalDate = LocalDate.now()
        ): DailyQuestSummary {
            val progresses = DailyQuestProgress.createForAllQuests(userId, dailyQuests, date)
            return DailyQuestSummary(
                userId = userId,
                date = date,
                progresses = progresses.toMutableList()
            )
        }
        
        /**
         * 기존 진행 상황으로부터 요약 재구성
         */
        fun fromProgresses(
            userId: UserId,
            date: LocalDate,
            progresses: List<DailyQuestProgress>
        ): DailyQuestSummary {
            return DailyQuestSummary(
                userId = userId,
                date = date,
                progresses = progresses.toMutableList()
            )
        }
    }
    
    /**
     * 완료된 퀘스트 수
     */
    fun getCompletedCount(): Int = progresses.count { it.isCompleted }
    
    /**
     * 전체 퀘스트 수
     */
    fun getTotalCount(): Int = progresses.size
    
    /**
     * 완료율 계산 (25% 단위)
     */
    fun getCompletionPercentage(): CompletionPercentage {
        return CompletionPercentage.from(getCompletedCount(), getTotalCount())
    }
    
    /**
     * 특정 퀘스트 완료 처리 및 보상 지급
     */
    fun completeQuest(questType: DailyQuestType): DailyQuestSummary {
        val progress = progresses.find { it.questType == questType }
            ?: throw IllegalArgumentException("Quest type not found: $questType")
        
        require(progress.canBeCompleted()) { "Quest cannot be completed" }
        
        // 퀘스트 완료 처리
        progress.complete()
        
        // 진행률 기반 특수 보상 확인
        val newPercentage = getCompletionPercentage()
        checkAndEarnSpecialRewards(newPercentage)
        
        updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 특정 퀘스트 완료 취소
     */
    fun uncompleteQuest(questType: DailyQuestType): DailyQuestSummary {
        val progress = progresses.find { it.questType == questType }
            ?: throw IllegalArgumentException("Quest type not found: $questType")
        
        require(progress.canBeUncompleted()) { "Quest cannot be uncompleted" }
        
        progress.uncomplete()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 진행률에 따른 특수 보상 확인 및 지급
     */
    private fun checkAndEarnSpecialRewards(currentPercentage: CompletionPercentage) {
        val availableRewards = SpecialRewardTier.getAvailableRewards(currentPercentage.value)
        val newRewards = availableRewards.filter { it !in specialRewardsEarned }
        
        specialRewardsEarned.addAll(newRewards)
    }
    
    /**
     * 기본 퀘스트 완료 보상 계산 (5포인트 × 완료 수)
     */
    fun getBaseRewardPoints(): Points {
        return Points(getCompletedCount() * 5)
    }
    
    /**
     * 특수 보상 포인트 계산
     */
    fun getSpecialRewardPoints(): Points {
        return Points(specialRewardsEarned.sumOf { it.basePoints })
    }
    
    /**
     * 총 보상 포인트 계산
     */
    fun getTotalRewardPoints(): Points {
        return Points(getBaseRewardPoints().value + getSpecialRewardPoints().value)
    }
    
    /**
     * 총 스탯 보상 계산 (완료한 퀘스트 수만큼 규율 스탯 증가)
     */
    fun getTotalStatReward(): Int = getCompletedCount()
    
    /**
     * 새로 획득한 특수 보상 반환
     */
    fun getNewSpecialRewards(): List<SpecialRewardTier> {
        return specialRewardsEarned.toList()
    }
    
    /**
     * 오늘의 퀘스트인지 확인
     */
    fun isToday(): Boolean = date == LocalDate.now()
    
    /**
     * 모든 퀘스트 완료 여부
     */
    fun isAllCompleted(): Boolean = getCompletedCount() == getTotalCount()
    
    /**
     * 특정 퀘스트의 완료 상태 확인
     */
    fun isQuestCompleted(questType: DailyQuestType): Boolean {
        return progresses.find { it.questType == questType }?.isCompleted ?: false
    }
    
    /**
     * 게임화된 상태 메시지 생성
     */
    fun getStatusMessage(): String {
        val percentage = getCompletionPercentage().value
        return when (percentage) {
            0 -> "🌅 새로운 하루의 시작! 퀘스트를 완료해보세요."
            25 -> "🥉 좋은 시작이에요! 계속해보세요."
            50 -> "🥈 절반 완료! 잘하고 있어요."
            75 -> "🥇 거의 다 왔어요! 마지막 퀘스트까지!"
            100 -> "💎 완벽한 하루! 모든 퀘스트를 완료했습니다!"
            else -> "🔥 진행 중... 삶을 게임처럼 즐겨보세요!"
        }
    }
}