package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.DailyQuestProgress
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestProgressId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import java.time.LocalDate

/**
 * 일일 퀘스트 진행 상황 Repository (Outbound Port)
 * 사용자별 특정 날짜의 개별 퀘스트 완료 여부 관리
 */
interface DailyQuestProgressRepository {
    
    /**
     * 일일 퀘스트 진행 상황 저장 (생성 및 수정)
     */
    fun save(progress: DailyQuestProgress): DailyQuestProgress
    
    /**
     * 진행 상황 ID로 조회
     */
    fun findById(progressId: DailyQuestProgressId): DailyQuestProgress?
    
    /**
     * 사용자의 특정 날짜 모든 퀘스트 진행 상황 조회
     */
    fun findByUserIdAndDate(userId: UserId, date: LocalDate): List<DailyQuestProgress>
    
    /**
     * 사용자의 특정 날짜, 특정 퀘스트 진행 상황 조회
     */
    fun findByUserIdAndDateAndQuestType(
        userId: UserId, 
        date: LocalDate, 
        questType: DailyQuestType
    ): DailyQuestProgress?
    
    /**
     * 사용자의 오늘 모든 퀘스트 진행 상황 조회
     */
    fun findTodayProgressByUserId(userId: UserId): List<DailyQuestProgress>
    
    /**
     * 사용자의 특정 기간 완료된 퀘스트 조회
     */
    fun findCompletedProgressByUserIdAndDateRange(
        userId: UserId, 
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<DailyQuestProgress>
    
    /**
     * 사용자의 특정 날짜 완료된 퀘스트 수 조회
     */
    fun countCompletedByUserIdAndDate(userId: UserId, date: LocalDate): Long
    
    /**
     * 사용자의 오늘 완료된 퀘스트 수 조회
     */
    fun countTodayCompletedByUserId(userId: UserId): Long
    
    /**
     * 사용자의 연속 완료 일수 계산용 데이터 조회
     */
    fun findRecentCompletionsByUserId(userId: UserId, days: Int): List<DailyQuestProgress>
    
    /**
     * 특정 퀘스트 타입의 전체 완료 통계 조회
     */
    fun countCompletedByQuestType(questType: DailyQuestType): Long
    
    /**
     * 특정 날짜의 전체 사용자 완료 통계 조회
     */
    fun countCompletedByDate(date: LocalDate): Long
    
    /**
     * 사용자의 특정 퀘스트 타입 완료 여부 확인
     */
    fun isQuestCompletedByUserToday(userId: UserId, questType: DailyQuestType): Boolean
    
    /**
     * 특정 진행 상황 삭제
     */
    fun deleteById(progressId: DailyQuestProgressId)
    
    /**
     * 사용자의 특정 날짜 모든 진행 상황 삭제
     */
    fun deleteByUserIdAndDate(userId: UserId, date: LocalDate)
    
    /**
     * 오래된 진행 상황 데이터 정리 (보관 기간 지난 데이터)
     */
    fun deleteProgressesOlderThan(date: LocalDate): Long
    
    /**
     * 사용자의 월별 완료 통계 조회
     */
    fun getMonthlyCompletionStats(userId: UserId, year: Int, month: Int): Map<DailyQuestType, Int>
    
    /**
     * 사용자의 연간 완료 통계 조회  
     */
    fun getYearlyCompletionStats(userId: UserId, year: Int): Map<DailyQuestType, Int>
}