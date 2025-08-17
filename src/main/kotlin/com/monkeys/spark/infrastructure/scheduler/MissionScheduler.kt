package com.monkeys.spark.infrastructure.scheduler

import com.monkeys.spark.application.port.`in`.MissionUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 미션 관련 스케줄링 작업
 * - 만료된 미션 자동 정리
 * - 미션 상태 자동 업데이트
 */
@Component
class MissionScheduler(
    private val missionUseCase: MissionUseCase
) {
    private val logger = LoggerFactory.getLogger(MissionScheduler::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * 매 시간마다 만료된 미션들을 자동으로 EXPIRED 상태로 변경
     * 크론 표현식: "0 0 * * * *" = 매 시간 정각
     */
    @Scheduled(cron = "0 0 * * * *")
    fun cleanupExpiredMissions() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("🕐 [MissionScheduler] Starting expired missions cleanup at {}", currentTime.format(formatter))
            
            val expiredCount = missionUseCase.cleanupExpiredMissions()
            
            if (expiredCount > 0) {
                logger.info("✅ [MissionScheduler] Successfully expired {} missions at {}", expiredCount, currentTime.format(formatter))
            } else {
                logger.debug("ℹ️ [MissionScheduler] No expired missions found at {}", currentTime.format(formatter))
            }
        } catch (exception: Exception) {
            logger.error("❌ [MissionScheduler] Error during expired missions cleanup", exception)
        }
    }

    /**
     * 매일 자정에 실행되는 일일 정리 작업
     * 크론 표현식: "0 0 0 * * *" = 매일 자정
     */
    @Scheduled(cron = "0 0 0 * * *")
    fun dailyMissionCleanup() {
        try {
            val currentTime = LocalDateTime.now()
            logger.info("🌙 [MissionScheduler] Starting daily mission cleanup at {}", currentTime.format(formatter))
            
            // 만료된 미션 정리
            val expiredCount = missionUseCase.cleanupExpiredMissions()
            
            logger.info("🎯 [MissionScheduler] Daily cleanup completed: {} missions expired", expiredCount)
        } catch (exception: Exception) {
            logger.error("❌ [MissionScheduler] Error during daily mission cleanup", exception)
        }
    }

    /**
     * 개발/테스트용: 5분마다 실행 (운영환경에서는 비활성화)
     * 이 메서드는 개발 시에만 사용하고, 운영에서는 주석 처리하거나 제거
     */
    // @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
    fun developmentMissionCheck() {
        if (isProduction()) {
            return // 운영환경에서는 실행하지 않음
        }
        
        try {
            val currentTime = LocalDateTime.now()
            logger.debug("🔧 [MissionScheduler] Development check at {}", currentTime.format(formatter))
            
            val expiredCount = missionUseCase.cleanupExpiredMissions()
            if (expiredCount > 0) {
                logger.debug("🔧 [MissionScheduler] Development check: {} missions expired", expiredCount)
            }
        } catch (exception: Exception) {
            logger.error("❌ [MissionScheduler] Error during development mission check", exception)
        }
    }

    /**
     * 운영환경 여부 확인
     */
    private fun isProduction(): Boolean {
        val profiles = System.getProperty("spring.profiles.active", "")
        return profiles.contains("prod") || profiles.contains("production")
    }
}