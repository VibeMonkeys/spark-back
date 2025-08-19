package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.DailyQuestSummary
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.SpecialRewardTier
import java.time.LocalDate

/**
 * 일일 퀘스트 요약 Repository (Outbound Port)
 * "삶을 게임처럼 즐겨라!" - 하루의 퀘스트 전체 현황과 보상 관리
 */
interface DailyQuestSummaryRepository {
    
    /**
     * 일일 퀘스트 요약 저장 (생성 및 수정)
     */
    fun save(summary: DailyQuestSummary): DailyQuestSummary
    
    /**
     * 사용자의 특정 날짜 요약 조회
     */
    fun findByUserIdAndDate(userId: UserId, date: LocalDate): DailyQuestSummary?
    
    /**
     * 사용자의 오늘 요약 조회
     */
    fun findTodaySummaryByUserId(userId: UserId): DailyQuestSummary?
    
    /**
     * 사용자의 특정 기간 요약들 조회
     */
    fun findByUserIdAndDateRange(
        userId: UserId, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<DailyQuestSummary>
    
    /**
     * 사용자의 최근 N일간 요약들 조회
     */
    fun findRecentSummariesByUserId(userId: UserId, days: Int): List<DailyQuestSummary>
    
    /**
     * 사용자의 완벽한 하루들 조회 (100% 완료한 날들)
     */
    fun findPerfectDaysByUserId(userId: UserId): List<DailyQuestSummary>
    
    /**
     * 사용자의 연속 완벽한 하루 수 계산
     */
    fun countConsecutivePerfectDays(userId: UserId): Long
    
    /**
     * 사용자의 월별 완료 통계 조회
     */
    fun getMonthlyStats(userId: UserId, year: Int, month: Int): Map<String, Any>
    
    /**
     * 사용자의 연간 완료 통계 조회
     */
    fun getYearlyStats(userId: UserId, year: Int): Map<String, Any>
    
    /**
     * 특정 날짜의 모든 사용자 완료율 분포 조회
     */
    fun getCompletionDistributionByDate(date: LocalDate): Map<Int, Long>
    
    /**
     * 최고 완료율 달성자들 조회 (리더보드용)
     */
    fun findTopPerformersByDate(date: LocalDate, limit: Int): List<DailyQuestSummary>
    
    /**
     * 최고 연속 완벽한 하루 달성자들 조회
     */
    fun findTopConsecutivePerfectDaysUsers(limit: Int): List<Pair<UserId, Long>>
    
    /**
     * 특정 보상 등급을 달성한 사용자 수 조회
     */
    fun countUsersWithRewardTier(rewardTier: SpecialRewardTier, date: LocalDate): Long
    
    /**
     * 사용자가 특정 날짜에 획득한 특수 보상들 조회
     */
    fun findSpecialRewardsByUserIdAndDate(userId: UserId, date: LocalDate): List<SpecialRewardTier>
    
    /**
     * 사용자의 총 특수 보상 획득 통계 조회
     */
    fun getTotalSpecialRewardStats(userId: UserId): Map<SpecialRewardTier, Long>
    
    /**
     * 요약 삭제
     */
    fun deleteByUserIdAndDate(userId: UserId, date: LocalDate)
    
    /**
     * 오래된 요약 데이터 정리 (보관 기간 지난 데이터)
     */
    fun deleteSummariesOlderThan(date: LocalDate): Long
    
    /**
     * 사용자의 평균 완료율 계산
     */
    fun calculateAverageCompletionRate(userId: UserId, days: Int): Double
    
    /**
     * 전체 사용자의 평균 완료율 계산
     */
    fun calculateGlobalAverageCompletionRate(date: LocalDate): Double
    
    /**
     * 사용자의 개선 추세 분석 (최근 완료율 증가/감소 여부)
     */
    fun analyzeImprovementTrend(userId: UserId, days: Int): String
    
    /**
     * 특정 완료율 이상 달성한 날들 조회
     */
    fun findDaysWithCompletionRateAbove(
        userId: UserId, 
        completionRate: Int, 
        days: Int
    ): List<DailyQuestSummary>
}