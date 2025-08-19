package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.application.port.`in`.dto.*
import com.monkeys.spark.domain.model.DailyQuestSummary
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import java.time.LocalDate

/**
 * 일일 퀘스트 UseCase 인터페이스 (Inbound Port)
 * "삶을 게임처럼 즐겨라!" - 매일의 루틴을 게임화하는 핵심 기능들
 */
interface DailyQuestUseCase {
    
    // ===============================================
    // 1. 기본 퀘스트 관리
    // ===============================================
    
    /**
     * 사용자의 오늘 일일 퀘스트 현황 조회
     */
    fun getTodayDailyQuests(query: GetTodayDailyQuestsQuery): DailyQuestProgressDto
    
    /**
     * 사용자의 특정 날짜 일일 퀘스트 현황 조회
     */
    fun getDailyQuestsByDate(query: GetDailyQuestsByDateQuery): DailyQuestProgressDto
    
    /**
     * 사용자의 일일 퀘스트 요약 조회 (게임화된 현황)
     */
    fun getDailyQuestSummary(userId: UserId, date: LocalDate = LocalDate.now()): DailyQuestSummaryDto
    
    // ===============================================
    // 2. 퀘스트 완료 처리
    // ===============================================
    
    /**
     * 일일 퀘스트 완료 처리
     * 포인트 지급, 스탯 증가, 진행률 기반 특수 보상 지급
     */
    fun completeDailyQuest(command: CompleteDailyQuestCommand): DailyQuestSummaryDto
    
    /**
     * 일일 퀘스트 완료 취소 (오늘 것만 가능)
     */
    fun uncompleteDailyQuest(command: UncompleteDailyQuestCommand): DailyQuestSummaryDto
    
    // ===============================================
    // 3. 퀘스트 초기화 (스케줄러용)
    // ===============================================
    
    /**
     * 사용자의 일일 퀘스트 초기화 (매일 자정 실행)
     */
    fun initializeDailyQuests(command: InitializeDailyQuestsCommand): DailyQuestProgressDto
    
    /**
     * 전체 사용자의 일일 퀘스트 초기화 (스케줄러용)
     */
    fun initializeAllUsersDailyQuests(command: InitializeAllUsersDailyQuestsCommand): Int
    
    // ===============================================
    // 4. 통계 및 분석
    // ===============================================
    
    /**
     * 사용자의 일일 퀘스트 통계 조회
     */
    fun getDailyQuestStats(query: GetDailyQuestStatsQuery): DailyQuestStatsDto
    
    /**
     * 사용자의 연속 완벽한 하루 조회
     */
    fun getConsecutivePerfectDays(query: GetConsecutivePerfectDaysQuery): Int
    
    /**
     * 사용자의 월별 일일 퀘스트 통계 조회
     */
    fun getMonthlyStats(query: GetMonthlyDailyQuestStatsQuery): MonthlyDailyQuestStatsDto
    
    /**
     * 사용자의 연간 일일 퀘스트 통계 조회
     */
    fun getYearlyStats(query: GetYearlyDailyQuestStatsQuery): Map<String, Any>
    
    /**
     * 사용자의 개선 추세 분석
     */
    fun getImprovementTrend(query: GetImprovementTrendQuery): String
    
    // ===============================================
    // 5. 리더보드 및 순위
    // ===============================================
    
    /**
     * 일일 퀘스트 리더보드 조회
     */
    fun getDailyQuestLeaderboard(query: GetDailyQuestLeaderboardQuery): DailyQuestLeaderboardDto
    
    /**
     * 전체 사용자의 완료 분포 조회
     */
    fun getGlobalCompletionDistribution(query: GetGlobalCompletionDistributionQuery): CompletionDistributionDto
    
    /**
     * 최고 연속 완벽한 하루 달성자들 조회
     */
    fun getTopConsecutivePerfectDaysUsers(limit: Int = 10): List<Pair<String, Long>>
    
    // ===============================================
    // 6. 관리자 기능 (퀘스트 템플릿 관리)
    // ===============================================
    
    /**
     * 일일 퀘스트 템플릿 생성 (관리자용)
     */
    fun createDailyQuestTemplate(command: CreateDailyQuestTemplateCommand): DailyQuestDto
    
    /**
     * 일일 퀘스트 템플릿 수정 (관리자용)
     */
    fun updateDailyQuestTemplate(command: UpdateDailyQuestTemplateCommand): DailyQuestDto
    
    /**
     * 모든 일일 퀘스트 템플릿 조회
     */
    fun getAllDailyQuestTemplates(): List<DailyQuestDto>
    
    // ===============================================
    // 7. 특수 보상 시스템
    // ===============================================
    
    /**
     * 특수 보상 지급 (진행률 달성 시)
     */
    fun grantSpecialReward(command: GrantSpecialRewardCommand): List<SpecialRewardDto>
    
    /**
     * 사용자의 특수 보상 통계 조회
     */
    fun getSpecialRewardStats(userId: UserId): Map<String, Any>
    
    /**
     * 특정 퀘스트 타입의 완료 통계 조회
     */
    fun getQuestTypeStats(query: GetQuestTypeStatsQuery): Map<String, Any>
    
    // ===============================================
    // 8. 게임화 요소
    // ===============================================
    
    /**
     * 사용자의 게임화된 상태 메시지 생성
     */
    fun getGameifiedStatusMessage(userId: UserId, date: LocalDate = LocalDate.now()): String
    
    /**
     * 다음 달성 가능한 마일스톤 정보 조회
     */
    fun getNextMilestone(userId: UserId, date: LocalDate = LocalDate.now()): SpecialRewardDto?
    
    /**
     * 사용자의 일일 퀘스트 성취도 분석
     */
    fun analyzeUserAchievement(userId: UserId, days: Int = 30): Map<String, Any>
}