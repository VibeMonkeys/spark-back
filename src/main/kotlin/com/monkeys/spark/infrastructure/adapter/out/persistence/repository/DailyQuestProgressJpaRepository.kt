package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.DailyQuestProgressEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * 일일 퀘스트 진행 상황 JPA Repository
 * 사용자별 특정 날짜의 개별 퀘스트 완료 여부 데이터 액세스
 */
@Repository
interface DailyQuestProgressJpaRepository : JpaRepository<DailyQuestProgressEntity, Long> {
    
    /**
     * 사용자의 특정 날짜 모든 퀘스트 진행 상황 조회
     */
    fun findByUserIdAndQuestDateOrderByDailyQuestId(userId: Long, questDate: LocalDate): List<DailyQuestProgressEntity>
    
    /**
     * 사용자의 특정 날짜, 특정 퀘스트 진행 상황 조회
     */
    fun findByUserIdAndQuestDateAndQuestType(
        userId: Long, 
        questDate: LocalDate, 
        questType: String
    ): DailyQuestProgressEntity?
    
    /**
     * 사용자의 오늘 모든 퀘스트 진행 상황 조회
     */
    @Query("SELECT dqp FROM DailyQuestProgressEntity dqp WHERE dqp.userId = :userId AND dqp.questDate = CURRENT_DATE ORDER BY dqp.dailyQuestId")
    fun findTodayProgressByUserId(@Param("userId") userId: Long): List<DailyQuestProgressEntity>
    
    /**
     * 사용자의 특정 기간 완료된 퀘스트 조회
     */
    @Query("SELECT dqp FROM DailyQuestProgressEntity dqp WHERE dqp.userId = :userId AND dqp.questDate BETWEEN :startDate AND :endDate AND dqp.isCompleted = true ORDER BY dqp.questDate DESC")
    fun findCompletedProgressByUserIdAndDateRange(
        @Param("userId") userId: Long, 
        @Param("startDate") startDate: LocalDate, 
        @Param("endDate") endDate: LocalDate
    ): List<DailyQuestProgressEntity>
    
    /**
     * 사용자의 특정 날짜 완료된 퀘스트 수 조회
     */
    @Query("SELECT COUNT(dqp) FROM DailyQuestProgressEntity dqp WHERE dqp.userId = :userId AND dqp.questDate = :questDate AND dqp.isCompleted = true")
    fun countCompletedByUserIdAndDate(
        @Param("userId") userId: Long, 
        @Param("questDate") questDate: LocalDate
    ): Long
    
    /**
     * 사용자의 오늘 완료된 퀘스트 수 조회
     */
    @Query("SELECT COUNT(dqp) FROM DailyQuestProgressEntity dqp WHERE dqp.userId = :userId AND dqp.questDate = CURRENT_DATE AND dqp.isCompleted = true")
    fun countTodayCompletedByUserId(@Param("userId") userId: Long): Long
    
    /**
     * 사용자의 연속 완료 일수 계산용 데이터 조회
     */
    @Query("SELECT dqp FROM DailyQuestProgressEntity dqp WHERE dqp.userId = :userId AND dqp.questDate >= :startDate AND dqp.isCompleted = true ORDER BY dqp.questDate DESC")
    fun findRecentCompletionsByUserId(
        @Param("userId") userId: Long, 
        @Param("startDate") startDate: LocalDate
    ): List<DailyQuestProgressEntity>
    
    /**
     * 특정 퀘스트 타입의 전체 완료 통계 조회
     */
    @Query("SELECT COUNT(dqp) FROM DailyQuestProgressEntity dqp WHERE dqp.questType = :questType AND dqp.isCompleted = true")
    fun countCompletedByQuestType(@Param("questType") questType: String): Long
    
    /**
     * 특정 날짜의 전체 사용자 완료 통계 조회
     */
    @Query("SELECT COUNT(dqp) FROM DailyQuestProgressEntity dqp WHERE dqp.questDate = :questDate AND dqp.isCompleted = true")
    fun countCompletedByDate(@Param("questDate") questDate: LocalDate): Long
    
    /**
     * 사용자의 특정 퀘스트 타입 완료 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(dqp) > 0 THEN true ELSE false END FROM DailyQuestProgressEntity dqp WHERE dqp.userId = :userId AND dqp.questDate = CURRENT_DATE AND dqp.questType = :questType AND dqp.isCompleted = true")
    fun isQuestCompletedByUserToday(
        @Param("userId") userId: Long, 
        @Param("questType") questType: String
    ): Boolean
    
    /**
     * 사용자의 특정 날짜 모든 진행 상황 삭제
     */
    fun deleteByUserIdAndQuestDate(userId: Long, questDate: LocalDate): Long
    
    /**
     * 오래된 진행 상황 데이터 정리 (보관 기간 지난 데이터)
     */
    @Query("DELETE FROM DailyQuestProgressEntity dqp WHERE dqp.questDate < :cutoffDate")
    fun deleteProgressesOlderThan(@Param("cutoffDate") cutoffDate: LocalDate): Long
    
    /**
     * 사용자의 월별 완료 통계 조회
     */
    @Query("""
        SELECT dqp.questType, COUNT(dqp) 
        FROM DailyQuestProgressEntity dqp 
        WHERE dqp.userId = :userId 
        AND YEAR(dqp.questDate) = :year 
        AND MONTH(dqp.questDate) = :month 
        AND dqp.isCompleted = true 
        GROUP BY dqp.questType
    """)
    fun getMonthlyCompletionStats(
        @Param("userId") userId: Long, 
        @Param("year") year: Int, 
        @Param("month") month: Int
    ): List<Array<Any>>
    
    /**
     * 사용자의 연간 완료 통계 조회
     */
    @Query("""
        SELECT dqp.questType, COUNT(dqp) 
        FROM DailyQuestProgressEntity dqp 
        WHERE dqp.userId = :userId 
        AND YEAR(dqp.questDate) = :year 
        AND dqp.isCompleted = true 
        GROUP BY dqp.questType
    """)
    fun getYearlyCompletionStats(
        @Param("userId") userId: Long, 
        @Param("year") year: Int
    ): List<Array<Any>>
    
    /**
     * 특정 기간의 일별 완료율 조회
     */
    @Query("""
        SELECT dqp.questDate, COUNT(CASE WHEN dqp.isCompleted = true THEN 1 END), COUNT(dqp)
        FROM DailyQuestProgressEntity dqp 
        WHERE dqp.userId = :userId 
        AND dqp.questDate BETWEEN :startDate AND :endDate
        GROUP BY dqp.questDate
        ORDER BY dqp.questDate DESC
    """)
    fun getDailyCompletionRates(
        @Param("userId") userId: Long, 
        @Param("startDate") startDate: LocalDate, 
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>
}