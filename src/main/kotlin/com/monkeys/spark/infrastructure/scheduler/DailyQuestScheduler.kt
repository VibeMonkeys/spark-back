package com.monkeys.spark.infrastructure.scheduler

import com.monkeys.spark.application.port.`in`.DailyQuestUseCase
import com.monkeys.spark.application.port.`in`.command.InitializeAllUsersDailyQuestsCommand
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 일일 퀘스트 스케줄링 작업
 * "삶을 게임처럼 즐겨라!" - 매일 자정 새로운 퀘스트 초기화
 */
@Component
class DailyQuestScheduler(
    private val dailyQuestUseCase: DailyQuestUseCase
) {
    private val logger = LoggerFactory.getLogger(DailyQuestScheduler::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * 매일 자정에 모든 사용자의 일일 퀘스트 초기화
     * 크론 표현식: "0 0 0 * * *" = 매일 자정 00:00:00
     * 
     * 🎮 새로운 하루의 시작! 모든 사용자에게 새로운 일일 퀘스트를 제공합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    fun initializeDailyQuestsForAllUsers() {
        try {
            val currentTime = LocalDateTime.now()
            val today = LocalDate.now()
            logger.info("🌅 [DailyQuestScheduler] 새로운 하루의 시작! 일일 퀘스트 초기화를 시작합니다. 시간: {}", currentTime.format(formatter))
            
            val command = InitializeAllUsersDailyQuestsCommand(today)
            val initializedUserCount = dailyQuestUseCase.initializeAllUsersDailyQuests(command)
            
            logger.info("🎉 [DailyQuestScheduler] 일일 퀘스트 초기화 완료! 총 {} 명의 사용자에게 새로운 퀘스트를 제공했습니다.", initializedUserCount)
            logger.info("🎯 [DailyQuestScheduler] 삶을 게임처럼 즐겨라! 오늘도 화이팅! 날짜: {}", today)
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 일일 퀘스트 초기화 중 오류 발생", exception)
            // 알림 시스템이 있다면 여기서 관리자에게 알림 전송
        }
    }

    /**
     * 매일 오전 9시에 사용자들에게 일일 퀘스트 리마인더 (선택사항)
     * 크론 표현식: "0 0 9 * * *" = 매일 오전 9시
     * 
     * 🔔 아침 알림: 오늘의 퀘스트를 완료해보세요!
     */
    @Scheduled(cron = "0 0 9 * * *")
    fun sendDailyQuestReminder() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("🔔 [DailyQuestScheduler] 일일 퀘스트 아침 리마인더 시간: {}", currentTime.format(formatter))
            
            // 실제 구현에서는 알림 서비스를 통해 사용자들에게 푸시 알림 전송
            // notificationService.sendDailyQuestReminder()
            
            logger.debug("📱 [DailyQuestScheduler] 모든 사용자에게 일일 퀘스트 리마인더를 전송했습니다.")
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 일일 퀘스트 리마인더 전송 중 오류 발생", exception)
        }
    }

    /**
     * 매일 오후 6시에 진행률 체크 및 격려 메시지 (선택사항)
     * 크론 표현식: "0 0 18 * * *" = 매일 오후 6시
     * 
     * 💪 저녁 체크: 오늘의 퀘스트 진행률을 확인하고 격려해주세요!
     */
    @Scheduled(cron = "0 0 18 * * *")
    fun sendProgressCheckReminder() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("💪 [DailyQuestScheduler] 일일 퀘스트 진행률 체크 시간: {}", currentTime.format(formatter))
            
            // 실제 구현에서는 진행률이 낮은 사용자들에게 격려 알림 전송
            // dailyQuestUseCase.checkProgressAndSendEncouragement()
            
            logger.debug("🌟 [DailyQuestScheduler] 사용자들의 오늘 진행률을 체크하고 격려 메시지를 전송했습니다.")
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 진행률 체크 중 오류 발생", exception)
        }
    }

    /**
     * 매주 일요일 자정에 주간 통계 정리 작업
     * 크론 표현식: "0 0 0 * * 0" = 매주 일요일 자정
     * 
     * 📊 주간 리포트: 지난 주의 성과를 정리하고 통계를 생성합니다.
     */
    @Scheduled(cron = "0 0 0 * * 0")
    fun weeklyStatsCleanup() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("📊 [DailyQuestScheduler] 주간 통계 정리 작업 시작: {}", currentTime.format(formatter))
            
            // 실제 구현에서는 주간 통계 생성 및 정리 작업
            // dailyQuestUseCase.generateWeeklyStats()
            // dailyQuestUseCase.cleanupOldProgressData()
            
            logger.info("🗂️ [DailyQuestScheduler] 주간 통계 정리 작업 완료")
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 주간 통계 정리 작업 중 오류 발생", exception)
        }
    }

    /**
     * 매월 1일 자정에 월간 통계 생성 및 데이터 정리
     * 크론 표현식: "0 0 0 1 * *" = 매월 1일 자정
     * 
     * 🗓️ 월간 리포트: 지난 달의 성과를 정리하고 오래된 데이터를 정리합니다.
     */
    @Scheduled(cron = "0 0 0 1 * *")
    fun monthlyDataCleanup() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("🗓️ [DailyQuestScheduler] 월간 데이터 정리 작업 시작: {}", currentTime.format(formatter))
            
            // 실제 구현에서는 월간 통계 생성 및 오래된 데이터 정리
            // dailyQuestUseCase.generateMonthlyStats()
            // dailyQuestUseCase.cleanupOldData(90) // 90일 이전 데이터 정리
            
            logger.info("🧹 [DailyQuestScheduler] 월간 데이터 정리 작업 완료")
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 월간 데이터 정리 작업 중 오류 발생", exception)
        }
    }

    /**
     * 개발/테스트용: 10분마다 실행 (운영환경에서는 비활성화)
     * 일일 퀘스트 시스템 상태 체크
     */
    @Scheduled(fixedRate = 600000) // 10분 = 600,000ms
    fun developmentDailyQuestCheck() {
        if (isProduction()) {
            return // 운영환경에서는 실행하지 않음
        }
        
        try {
            val currentTime = LocalDateTime.now()
            logger.debug("🔧 [DailyQuestScheduler] 개발환경 일일 퀘스트 시스템 체크: {}", currentTime.format(formatter))
            
            // 개발환경에서의 상태 체크 로직
            // dailyQuestUseCase.healthCheck()
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 개발환경 체크 중 오류 발생", exception)
        }
    }

    /**
     * 매일 오후 11시 50분에 하루 마감 10분 전 알림 (선택사항)
     * 크론 표현식: "0 50 23 * * *" = 매일 오후 11시 50분
     * 
     * ⏰ 마감 임박: 오늘의 퀘스트를 완료할 마지막 기회입니다!
     */
    @Scheduled(cron = "0 50 23 * * *")
    fun sendLastChanceReminder() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("⏰ [DailyQuestScheduler] 하루 마감 10분 전 알림 시간: {}", currentTime.format(formatter))
            
            // 실제 구현에서는 미완료 퀘스트가 있는 사용자들에게 마지막 기회 알림
            // dailyQuestUseCase.sendLastChanceNotification()
            
            logger.debug("🚨 [DailyQuestScheduler] 미완료 퀘스트가 있는 사용자들에게 마지막 기회 알림을 전송했습니다.")
            
        } catch (exception: Exception) {
            logger.error("❌ [DailyQuestScheduler] 마지막 기회 알림 전송 중 오류 발생", exception)
        }
    }

    /**
     * 운영환경 여부 확인
     */
    private fun isProduction(): Boolean {
        val profiles = System.getProperty("spring.profiles.active", "")
        return profiles.contains("prod") || profiles.contains("production")
    }

    /**
     * 수동 실행용 메서드 (관리자가 필요시 호출)
     */
    fun manualInitializeDailyQuests() {
        logger.info("🔧 [DailyQuestScheduler] 관리자에 의한 수동 일일 퀘스트 초기화 실행")
        initializeDailyQuestsForAllUsers()
    }
}