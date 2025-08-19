package com.monkeys.spark.domain.vo.dailyquest

import com.monkeys.spark.domain.vo.common.Points
import java.util.UUID

/**
 * 일일 퀘스트 Value Objects
 * "삶을 게임처럼 즐겨라!" - 일상을 게임화하는 핵심 요소들
 */

@JvmInline
value class DailyQuestId(val value: String) {
    companion object {
        fun generate(): DailyQuestId = DailyQuestId(UUID.randomUUID().toString())
        fun from(value: String): DailyQuestId = DailyQuestId(value)
    }
}

@JvmInline
value class DailyQuestProgressId(val value: String) {
    companion object {
        fun generate(): DailyQuestProgressId = DailyQuestProgressId(UUID.randomUUID().toString())
    }
}

/**
 * 일일 퀘스트 타입 - 삶의 기본 루틴들
 */
enum class DailyQuestType(
    val title: String,
    val description: String,
    val icon: String,
    val order: Int
) {
    MAKE_BED("이불 개기", "일어나서 이불을 정리하세요", "🛏️", 1),
    TAKE_SHOWER("샤워하기", "깔끔하게 샤워를 하세요", "🚿", 2),
    CLEAN_HOUSE("집 청소하기", "주변을 깨끗하게 정리하세요", "🧹", 3),
    GRATITUDE_JOURNAL("감사 일기", "감사한 일 한 가지를 생각해보세요", "🙏", 4);

    companion object {
        fun getAllQuests(): List<DailyQuestType> = values().sortedBy { it.order }
    }
}

/**
 * 일일 퀘스트 완료율 (0%, 25%, 50%, 75%, 100%)
 */
@JvmInline
value class CompletionPercentage(val value: Int) {
    init {
        require(value in 0..100) { "Completion percentage must be between 0 and 100" }
        require(value % 25 == 0) { "Completion percentage must be in 25% increments" }
    }

    fun isComplete(): Boolean = value == 100
    
    fun getNextMilestone(): Int? = when (value) {
        0 -> 25
        25 -> 50
        50 -> 75
        75 -> 100
        else -> null
    }
    
    companion object {
        fun from(completedCount: Int, totalCount: Int): CompletionPercentage {
            val percentage = if (totalCount == 0) 0 else (completedCount * 100) / totalCount
            // 25% 단위로 내림
            val adjustedPercentage = (percentage / 25) * 25
            return CompletionPercentage(adjustedPercentage)
        }
        
        fun zero(): CompletionPercentage = CompletionPercentage(0)
        fun complete(): CompletionPercentage = CompletionPercentage(100)
    }
}

/**
 * 특수 보상 등급 (20%, 40%, 60%, 80%, 100% 달성 시 지급)
 */
enum class SpecialRewardTier(
    val requiredPercentage: Int,
    val basePoints: Int,
    val description: String,
    val emoji: String
) {
    BRONZE(25, 10, "첫 걸음 보상", "🥉"),
    SILVER(50, 25, "절반 달성 보상", "🥈"),
    GOLD(75, 50, "거의 다 왔어요!", "🥇"),
    PLATINUM(100, 100, "완벽한 하루!", "💎");

    companion object {
        fun getRewardForPercentage(percentage: Int): SpecialRewardTier? {
            return values().find { it.requiredPercentage == percentage }
        }
        
        fun getAvailableRewards(percentage: Int): List<SpecialRewardTier> {
            return values().filter { it.requiredPercentage <= percentage }
        }
    }
}

/**
 * 일일 퀘스트 보상 정보
 */
data class DailyQuestReward(
    val basePoints: Points = Points(5), // 각 퀘스트 완료 시 5포인트
    val statBonus: Int = 1, // 스탯 1 증가
    val specialRewards: List<SpecialRewardTier> = emptyList()
) {
    fun getTotalPoints(): Points {
        val specialBonus = specialRewards.sumOf { it.basePoints }
        return Points(basePoints.value + specialBonus)
    }
}

/**
 * 일일 퀘스트 스탯 타입 - 완료한 퀘스트에 따라 증가할 스탯
 */
enum class DailyQuestStatType(val displayName: String) {
    DISCIPLINE("규율"); // 일일 퀘스트는 모두 규율 스탯 증가
    
    companion object {
        fun getDefault(): DailyQuestStatType = DISCIPLINE
    }
}