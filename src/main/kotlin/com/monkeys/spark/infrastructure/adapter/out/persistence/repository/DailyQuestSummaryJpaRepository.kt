package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.DailyQuestSummaryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 일일 퀘스트 요약 JPA Repository
 * "삶을 게임처럼 즐겨라!" - 하루의 퀘스트 전체 현황과 보상 데이터 액세스
 */
@Repository
interface DailyQuestSummaryJpaRepository : JpaRepository<DailyQuestSummaryEntity, Long> {
    
    /**
     * 사용자의 특정 날짜 요약 조회
     */
    fun findByUserIdAndSummaryDate(userId: Long, summaryDate: LocalDate): DailyQuestSummaryEntity?
    
    /**
     * 사용자의 오늘 요약 조회
     */
    @Query("SELECT dqs FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId AND dqs.summaryDate = CURRENT_DATE")
    fun findTodaySummaryByUserId(@Param("userId") userId: Long): DailyQuestSummaryEntity?
    
    /**
     * 사용자의 특정 기간 요약들 조회
     */
    @Query("SELECT dqs FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId AND dqs.summaryDate BETWEEN :startDate AND :endDate ORDER BY dqs.summaryDate DESC")
    fun findByUserIdAndDateRange(
        @Param("userId") userId: Long, 
        @Param("startDate") startDate: LocalDate, 
        @Param("endDate") endDate: LocalDate
    ): List<DailyQuestSummaryEntity>
    
    /**
     * 사용자의 최근 N일간 요약들 조회
     */
    @Query("SELECT dqs FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId ORDER BY dqs.summaryDate DESC LIMIT :days")
    fun findRecentSummariesByUserId(@Param("userId") userId: Long, @Param("days") days: Int): List<DailyQuestSummaryEntity>
    
    /**
     * 사용자의 완벽한 하루들 조회 (100% 완료한 날들)
     */
    @Query("SELECT dqs FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId AND dqs.completionPercentage = 100 ORDER BY dqs.summaryDate DESC")
    fun findPerfectDaysByUserId(@Param("userId") userId: Long): List<DailyQuestSummaryEntity>
    
    /**
     * 사용자의 연속 완벽한 하루 수 계산
     */
    @Query("""
        SELECT COUNT(dqs)
        FROM DailyQuestSummaryEntity dqs
        WHERE dqs.userId = :userId 
        AND dqs.completionPercentage = 100
        AND dqs.summaryDate >= (
            SELECT MAX(dqs2.summaryDate)
            FROM DailyQuestSummaryEntity dqs2
            WHERE dqs2.userId = :userId 
            AND dqs2.completionPercentage < 100
            AND dqs2.summaryDate <= CURRENT_DATE
        )
        AND dqs.summaryDate <= CURRENT_DATE
    """)
    fun countConsecutivePerfectDays(@Param("userId") userId: Long): Long
    
    /**
     * 사용자의 월별 완료 통계 조회
     */
    @Query("""
        SELECT 
            COUNT(*) as totalDays,
            COUNT(CASE WHEN dqs.completedCount > 0 THEN 1 END) as completedDays,
            COUNT(CASE WHEN dqs.completionPercentage = 100 THEN 1 END) as perfectDays,
            AVG(dqs.completionPercentage) as averageCompletionRate,
            SUM(dqs.totalRewardPoints) as totalRewardPoints
        FROM DailyQuestSummaryEntity dqs 
        WHERE dqs.userId = :userId 
        AND EXTRACT(YEAR FROM dqs.summaryDate) = :year 
        AND EXTRACT(MONTH FROM dqs.summaryDate) = :month
    """)
    fun getMonthlyStats(@Param("userId") userId: Long, @Param("year") year: Int, @Param("month") month: Int): Array<Any>
    
    /**
     * 사용자의 연간 완료 통계 조회
     */
    @Query("""
        SELECT 
            COUNT(*) as totalDays,
            COUNT(CASE WHEN dqs.completedCount > 0 THEN 1 END) as completedDays,
            COUNT(CASE WHEN dqs.completionPercentage = 100 THEN 1 END) as perfectDays,
            AVG(dqs.completionPercentage) as averageCompletionRate,
            SUM(dqs.totalRewardPoints) as totalRewardPoints
        FROM DailyQuestSummaryEntity dqs 
        WHERE dqs.userId = :userId 
        AND EXTRACT(YEAR FROM dqs.summaryDate) = :year
    """)
    fun getYearlyStats(@Param("userId") userId: Long, @Param("year") year: Int): Array<Any>
    
    /**
     * 특정 날짜의 모든 사용자 완료율 분포 조회
     */
    @Query("""
        SELECT dqs.completionPercentage, COUNT(dqs)
        FROM DailyQuestSummaryEntity dqs 
        WHERE dqs.summaryDate = :date
        GROUP BY dqs.completionPercentage
        ORDER BY dqs.completionPercentage
    """)
    fun getCompletionDistributionByDate(@Param("date") date: LocalDate): List<Array<Any>>
    
    /**
     * 최고 완료율 달성자들 조회 (리더보드용)
     */
    @Query("SELECT dqs FROM DailyQuestSummaryEntity dqs WHERE dqs.summaryDate = :date ORDER BY dqs.completionPercentage DESC, dqs.totalRewardPoints DESC")
    fun findTopPerformersByDate(@Param("date") date: LocalDate): List<DailyQuestSummaryEntity>
    
    /**
     * 최고 연속 완벽한 하루 달성자들 조회
     */
    @Query("""
        SELECT dqs.userId, COUNT(dqs.userId) as consecutive_days
        FROM DailyQuestSummaryEntity dqs
        WHERE dqs.completionPercentage = 100
        GROUP BY dqs.userId
        ORDER BY consecutive_days DESC
    """)
    fun findTopConsecutivePerfectDaysUsers(): List<Array<Any>>
    
    /**
     * 특정 보상 등급을 달성한 사용자 수 조회
     */
    @Query("SELECT COUNT(DISTINCT dqs.userId) FROM DailyQuestSummaryEntity dqs WHERE dqs.summaryDate = :date AND dqs.specialRewardsEarned LIKE %:rewardTier%")
    fun countUsersWithRewardTier(@Param("rewardTier") rewardTier: String, @Param("date") date: LocalDate): Long
    
    /**
     * 사용자의 특정 날짜에 획득한 특수 보상들 조회
     */
    @Query("SELECT dqs.specialRewardsEarned FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId AND dqs.summaryDate = :date")
    fun findSpecialRewardsByUserIdAndDate(@Param("userId") userId: Long, @Param("date") date: LocalDate): String?
    
    /**
     * 사용자의 총 특수 보상 획득 통계 조회
     */
    @Query("""
        SELECT dqs.specialRewardsEarned
        FROM DailyQuestSummaryEntity dqs 
        WHERE dqs.userId = :userId 
        AND dqs.specialRewardsEarned IS NOT NULL 
        AND dqs.specialRewardsEarned != '[]'
    """)
    fun getTotalSpecialRewardStats(@Param("userId") userId: Long): List<String>
    
    /**
     * 사용자의 특정 날짜 요약 삭제
     */
    fun deleteByUserIdAndSummaryDate(userId: Long, summaryDate: LocalDate): Long
    
    /**
     * 오래된 요약 데이터 정리 (보관 기간 지난 데이터)
     */
    @Query("DELETE FROM DailyQuestSummaryEntity dqs WHERE dqs.summaryDate < :cutoffDate")
    fun deleteSummariesOlderThan(@Param("cutoffDate") cutoffDate: LocalDate): Long
    
    /**
     * 사용자의 평균 완료율 계산
     */
    @Query("SELECT AVG(dqs.completionPercentage) FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId AND dqs.summaryDate >= :startDate")
    fun calculateAverageCompletionRate(@Param("userId") userId: Long, @Param("startDate") startDate: LocalDate): Double?
    
    /**
     * 전체 사용자의 평균 완료율 계산
     */
    @Query("SELECT AVG(dqs.completionPercentage) FROM DailyQuestSummaryEntity dqs WHERE dqs.summaryDate = :date")
    fun calculateGlobalAverageCompletionRate(@Param("date") date: LocalDate): Double?
    
    /**
     * 특정 완료율 이상 달성한 날들 조회
     */
    @Query("SELECT dqs FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId AND dqs.completionPercentage >= :completionRate AND dqs.summaryDate >= :startDate ORDER BY dqs.summaryDate DESC")
    fun findDaysWithCompletionRateAbove(
        @Param("userId") userId: Long, 
        @Param("completionRate") completionRate: Int, 
        @Param("startDate") startDate: LocalDate
    ): List<DailyQuestSummaryEntity>
    
    /**
     * 사용자의 개선 추세 분석용 최근 데이터 조회
     */
    @Query("SELECT dqs.completionPercentage FROM DailyQuestSummaryEntity dqs WHERE dqs.userId = :userId ORDER BY dqs.summaryDate DESC LIMIT :days")
    fun getRecentCompletionRatesForTrend(@Param("userId") userId: Long, @Param("days") days: Int): List<Int>
    
    /**
     * Native UPDATE: 기존 Summary 직접 업데이트 (제약조건 위반 완전 해결)
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE daily_quest_summary SET
            completed_count = :completedCount,
            total_count = :totalCount,
            completion_percentage = :completionPercentage,
            base_reward_points = :baseRewardPoints,
            special_reward_points = :specialRewardPoints,
            total_reward_points = :totalRewardPoints,
            special_rewards_earned = :specialRewardsEarned,
            total_stat_reward = :totalStatReward,
            status_message = :statusMessage,
            updated_at = :updatedAt
        WHERE user_id = :userId AND summary_date = :summaryDate
    """, nativeQuery = true)
    fun updateByUserIdAndDate(
        @Param("userId") userId: Long,
        @Param("summaryDate") summaryDate: LocalDate,
        @Param("completedCount") completedCount: Int,
        @Param("totalCount") totalCount: Int,
        @Param("completionPercentage") completionPercentage: Int,
        @Param("baseRewardPoints") baseRewardPoints: Int,
        @Param("specialRewardPoints") specialRewardPoints: Int,
        @Param("totalRewardPoints") totalRewardPoints: Int,
        @Param("specialRewardsEarned") specialRewardsEarned: String,
        @Param("totalStatReward") totalStatReward: Int,
        @Param("statusMessage") statusMessage: String,
        @Param("updatedAt") updatedAt: java.time.LocalDateTime
    ): Int
}