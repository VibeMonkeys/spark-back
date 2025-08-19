package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.dailyquest.*
import com.monkeys.spark.domain.vo.stat.StatType

/**
 * 일일 퀘스트 보상 Domain Service
 * "삶을 게임처럼 즐겨라!" - 진행률 기반 특수 보상 시스템의 핵심 비즈니스 로직
 */
class DailyQuestRewardDomainService {
    
    /**
     * 퀘스트 완료 시 사용자에게 보상 지급
     */
    fun grantQuestCompletionReward(user: User, userStats: UserStats?): Pair<User, UserStats?> {
        // 기본 보상: 5포인트 + 규율 스탯 1 증가
        val updatedUser = user.earnPoints(Points(5))
        
        val updatedUserStats = userStats?.let {
            it.addStatValue(StatType.DISCIPLINE, 1)
            it
        }
        
        return updatedUser to updatedUserStats
    }
    
    /**
     * 진행률 달성 시 특수 보상 계산 및 지급
     */
    fun grantProgressMilestoneReward(
        user: User, 
        userStats: UserStats?,
        milestone: SpecialRewardTier
    ): Pair<User, UserStats?> {
        // 특수 보상 포인트 지급
        val updatedUser = user.earnPoints(Points(milestone.basePoints))
        
        // 마일스톤별 추가 스탯 보너스
        val updatedUserStats = userStats?.let {
            val bonusStats = calculateMilestoneStatBonus(milestone)
            it.addStatValue(StatType.DISCIPLINE, bonusStats)
            it
        }
        
        return updatedUser to updatedUserStats
    }
    
    /**
     * 연속 완벽한 하루 달성 시 보너스 보상
     */
    fun grantConsecutivePerfectDayBonus(
        user: User,
        userStats: UserStats?,
        consecutiveDays: Int
    ): Pair<User, UserStats?> {
        val bonusReward = calculateConsecutiveDayBonus(consecutiveDays)
        val updatedUser = user.earnPoints(bonusReward)
        
        // 연속일 수에 따른 추가 스탯 보너스
        val updatedUserStats = userStats?.let {
            val bonusStats = when {
                consecutiveDays >= 30 -> 5  // 30일 이상: 규율 +5
                consecutiveDays >= 14 -> 3  // 14일 이상: 규율 +3
                consecutiveDays >= 7 -> 2   // 7일 이상: 규율 +2
                consecutiveDays >= 3 -> 1   // 3일 이상: 규율 +1
                else -> 0
            }
            it.addStatValue(StatType.DISCIPLINE, bonusStats)
            it
        }
        
        return updatedUser to updatedUserStats
    }
    
    /**
     * 주간 성과 기반 보너스 계산
     */
    fun calculateWeeklyPerformanceBonus(weeklyCompletionRate: Double): Points {
        return when {
            weeklyCompletionRate >= 0.95 -> Points(200)  // 95% 이상: 200포인트
            weeklyCompletionRate >= 0.85 -> Points(150)  // 85% 이상: 150포인트
            weeklyCompletionRate >= 0.75 -> Points(100)  // 75% 이상: 100포인트
            weeklyCompletionRate >= 0.60 -> Points(50)   // 60% 이상: 50포인트
            else -> Points(0)
        }
    }
    
    /**
     * 월간 성과 기반 보너스 계산
     */
    fun calculateMonthlyPerformanceBonus(monthlyCompletionRate: Double, perfectDays: Int): Points {
        val baseBonus = when {
            monthlyCompletionRate >= 0.90 -> 500
            monthlyCompletionRate >= 0.80 -> 400
            monthlyCompletionRate >= 0.70 -> 300
            monthlyCompletionRate >= 0.60 -> 200
            monthlyCompletionRate >= 0.50 -> 100
            else -> 0
        }
        
        // 완벽한 하루 추가 보너스
        val perfectDayBonus = perfectDays * 20  // 완벽한 하루당 20포인트 추가
        
        return Points(baseBonus + perfectDayBonus)
    }
    
    /**
     * 특별 이벤트 보상 계산 (계절별, 기념일 등)
     */
    fun calculateSpecialEventReward(eventType: String, baseCompletionRate: Double): Points {
        val multiplier = when (eventType) {
            "NEW_YEAR" -> 3.0      // 신년 이벤트: 3배
            "BIRTHDAY" -> 2.5      // 생일 이벤트: 2.5배
            "HOLIDAY" -> 2.0       // 공휴일 이벤트: 2배
            "WEEKEND" -> 1.5       // 주말 이벤트: 1.5배
            else -> 1.0
        }
        
        val baseReward = (baseCompletionRate * 100).toInt()
        return Points((baseReward * multiplier).toInt())
    }
    
    /**
     * 리더보드 순위 기반 보상 계산
     */
    fun calculateLeaderboardReward(rank: Int, totalParticipants: Int): Points {
        val topPercentage = (rank.toDouble() / totalParticipants) * 100
        
        return when {
            rank == 1 -> Points(1000)                    // 1위: 1000포인트
            rank <= 3 -> Points(500)                     // 2-3위: 500포인트
            rank <= 10 -> Points(300)                    // 4-10위: 300포인트
            topPercentage <= 10 -> Points(200)           // 상위 10%: 200포인트
            topPercentage <= 25 -> Points(100)           // 상위 25%: 100포인트
            topPercentage <= 50 -> Points(50)            // 상위 50%: 50포인트
            else -> Points(10)                            // 참여 보상: 10포인트
        }
    }
    
    /**
     * 개선 추세 기반 격려 보상
     */
    fun calculateImprovementReward(improvementTrend: String): Points {
        return when (improvementTrend) {
            "📈 큰 향상" -> Points(100)
            "📊 향상 중" -> Points(75)
            "🔥 조금씩 향상" -> Points(50)
            "➡️ 유지" -> Points(25)
            else -> Points(10)  // 격려 보상
        }
    }
    
    /**
     * 사용자 레벨 기반 보상 배수 계산
     */
    fun calculateLevelMultiplier(userLevel: Int): Double {
        return when {
            userLevel >= 50 -> 2.0    // 50레벨 이상: 2배
            userLevel >= 40 -> 1.8    // 40레벨 이상: 1.8배
            userLevel >= 30 -> 1.6    // 30레벨 이상: 1.6배
            userLevel >= 20 -> 1.4    // 20레벨 이상: 1.4배
            userLevel >= 10 -> 1.2    // 10레벨 이상: 1.2배
            else -> 1.0               // 10레벨 미만: 기본
        }
    }
    
    /**
     * 도전 과제 달성 보상 (추가 업적 시스템)
     */
    fun calculateChallengeReward(challengeType: String, achievementLevel: Int): Points {
        val baseReward = when (challengeType) {
            "EARLY_BIRD" -> 50      // 아침형 인간 (오전 8시 전 완료)
            "NIGHT_OWL" -> 30       // 올빼미족 (오후 10시 후 완료)
            "WEEKEND_WARRIOR" -> 75 // 주말 전사 (주말에도 성실)
            "PERFECTIONIST" -> 100  // 완벽주의자 (연속 완벽한 하루)
            "CONSISTENT" -> 80      // 꾸준함 (일정한 완료율 유지)
            else -> 20
        }
        
        return Points(baseReward * achievementLevel)
    }
    
    // ===============================================
    // Private Helper Methods
    // ===============================================
    
    private fun calculateMilestoneStatBonus(milestone: SpecialRewardTier): Int {
        return when (milestone) {
            SpecialRewardTier.BRONZE -> 1
            SpecialRewardTier.SILVER -> 2
            SpecialRewardTier.GOLD -> 3
            SpecialRewardTier.PLATINUM -> 5
        }
    }
    
    private fun calculateConsecutiveDayBonus(consecutiveDays: Int): Points {
        return when {
            consecutiveDays >= 100 -> Points(1000)  // 100일: 1000포인트
            consecutiveDays >= 50 -> Points(500)    // 50일: 500포인트
            consecutiveDays >= 30 -> Points(300)    // 30일: 300포인트
            consecutiveDays >= 14 -> Points(200)    // 14일: 200포인트
            consecutiveDays >= 7 -> Points(100)     // 7일: 100포인트
            consecutiveDays >= 3 -> Points(50)      // 3일: 50포인트
            else -> Points(20)                       // 기본: 20포인트
        }
    }
    
    /**
     * 게임화된 보상 메시지 생성
     */
    fun generateRewardMessage(
        rewardType: String,
        points: Points,
        milestone: SpecialRewardTier? = null
    ): String {
        return when (rewardType) {
            "QUEST_COMPLETION" -> "🎉 퀘스트 완료! +${points.value}포인트, +1 규율 스탯"
            "MILESTONE" -> "🏆 ${milestone?.emoji} ${milestone?.description}! +${points.value}포인트 보너스!"
            "CONSECUTIVE" -> "🔥 연속 완벽한 하루 달성! +${points.value}포인트 보너스!"
            "WEEKLY" -> "📅 주간 성과 보너스! +${points.value}포인트"
            "MONTHLY" -> "🗓️ 월간 성과 보너스! +${points.value}포인트"
            "LEADERBOARD" -> "🏅 리더보드 순위 보상! +${points.value}포인트"
            "IMPROVEMENT" -> "📈 성장하는 모습이 멋져요! +${points.value}포인트"
            else -> "✨ 보상 획득! +${points.value}포인트"
        }
    }
}