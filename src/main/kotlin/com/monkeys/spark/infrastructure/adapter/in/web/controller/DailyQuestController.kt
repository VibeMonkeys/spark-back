package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.DailyQuestUseCase
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.CompleteDailyQuestRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * 일일 퀘스트 REST Controller
 * "삶을 게임처럼 즐겨라!" - 매일의 루틴을 게임화하는 API 엔드포인트
 */
@RestController
@RequestMapping("/api/v1/daily-quests")
class DailyQuestController(
    private val dailyQuestUseCase: DailyQuestUseCase
) {

    // ===============================================
    // 1. 기본 퀘스트 조회 API
    // ===============================================

    /**
     * 오늘의 일일 퀘스트 현황 조회
     * GET /api/v1/daily-quests/today?userId={userId}
     */
    @GetMapping("/today")
    fun getTodayDailyQuests(@RequestParam userId: Long): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val query = GetTodayDailyQuestsQuery(userIdVO)
        val result = dailyQuestUseCase.getTodayDailyQuests(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "오늘의 일일 퀘스트를 조회했습니다."
            )
        )
    }

    /**
     * 특정 날짜의 일일 퀘스트 현황 조회
     * GET /api/v1/daily-quests/date/{date}?userId={userId}
     */
    @GetMapping("/date/{date}")
    fun getDailyQuestsByDate(
        @PathVariable date: String,
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val targetDate = LocalDate.parse(date)
        val query = GetDailyQuestsByDateQuery(userIdVO, targetDate)
        val result = dailyQuestUseCase.getDailyQuestsByDate(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "${date}의 일일 퀘스트를 조회했습니다."
            )
        )
    }

    /**
     * 일일 퀘스트 요약 조회 (게임화된 현황)
     * GET /api/v1/daily-quests/summary?userId={userId}&date={date}
     */
    @GetMapping("/summary")
    fun getDailyQuestSummary(
        @RequestParam userId: Long,
        @RequestParam(required = false) date: String?
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val targetDate = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val result = dailyQuestUseCase.getDailyQuestSummary(userIdVO, targetDate)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "일일 퀘스트 요약을 조회했습니다."
            )
        )
    }

    // ===============================================
    // 2. 퀘스트 완료 처리 API
    // ===============================================

    /**
     * 일일 퀘스트 완료 처리
     * POST /api/v1/daily-quests/complete
     */
    @PostMapping("/complete")
    fun completeDailyQuest(@RequestBody request: CompleteDailyQuestRequest): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(request.userId.toString())
        val questType = DailyQuestType.valueOf(request.questType)
        val command = CompleteDailyQuestCommand(userIdVO, questType)
        val result = dailyQuestUseCase.completeDailyQuest(command)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "🎉 ${questType.title}을(를) 완료했습니다! 포인트와 스탯을 획득했어요."
            )
        )
    }

    /**
     * 일일 퀘스트 완료 취소 (오늘 것만 가능)
     * DELETE /api/v1/daily-quests/complete
     */
    @DeleteMapping("/complete")
    fun uncompleteDailyQuest(@RequestBody request: CompleteDailyQuestRequest): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(request.userId.toString())
        val questType = DailyQuestType.valueOf(request.questType)
        val command = UncompleteDailyQuestCommand(userIdVO, questType)
        val result = dailyQuestUseCase.uncompleteDailyQuest(command)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "${questType.title} 완료를 취소했습니다."
            )
        )
    }

    // ===============================================
    // 3. 통계 및 분석 API
    // ===============================================

    /**
     * 일일 퀘스트 통계 조회
     * GET /api/v1/daily-quests/stats?userId={userId}&days={days}
     */
    @GetMapping("/stats")
    fun getDailyQuestStats(
        @RequestParam userId: Long,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong())
        val query = GetDailyQuestStatsQuery(userIdVO, startDate, endDate)
        val result = dailyQuestUseCase.getDailyQuestStats(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "일일 퀘스트 통계를 조회했습니다."
            )
        )
    }

    /**
     * 연속 완벽한 하루 조회
     * GET /api/v1/daily-quests/consecutive-perfect-days?userId={userId}
     */
    @GetMapping("/consecutive-perfect-days")
    fun getConsecutivePerfectDays(@RequestParam userId: Long): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val query = GetConsecutivePerfectDaysQuery(userIdVO)
        val result = dailyQuestUseCase.getConsecutivePerfectDays(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = mapOf(
                    "consecutivePerfectDays" to result,
                    "message" to if (result > 0) "🔥 $result 일 연속 완벽한 하루!" else "첫 완벽한 하루를 만들어보세요!"
                ),
                message = "연속 완벽한 하루를 조회했습니다."
            )
        )
    }

    /**
     * 월별 일일 퀘스트 통계 조회
     * GET /api/v1/daily-quests/monthly-stats?userId={userId}&year={year}&month={month}
     */
    @GetMapping("/monthly-stats")
    fun getMonthlyStats(
        @RequestParam userId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val query = GetMonthlyDailyQuestStatsQuery(userIdVO, year, month)
        val result = dailyQuestUseCase.getMonthlyStats(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "${year}년 ${month}월 일일 퀘스트 통계를 조회했습니다."
            )
        )
    }

    /**
     * 개선 추세 분석 조회
     * GET /api/v1/daily-quests/improvement-trend?userId={userId}&days={days}
     */
    @GetMapping("/improvement-trend")
    fun getImprovementTrend(
        @RequestParam userId: Long,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val query = GetImprovementTrendQuery(userIdVO, days)
        val result = dailyQuestUseCase.getImprovementTrend(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = mapOf(
                    "trend" to result,
                    "analysis" to "최근 ${days}일간의 완료율 변화 추세입니다."
                ),
                message = "개선 추세를 분석했습니다."
            )
        )
    }

    // ===============================================
    // 4. 리더보드 및 순위 API
    // ===============================================

    /**
     * 일일 퀘스트 리더보드 조회
     * GET /api/v1/daily-quests/leaderboard?date={date}&limit={limit}
     */
    @GetMapping("/leaderboard")
    fun getDailyQuestLeaderboard(
        @RequestParam(required = false) date: String?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val targetDate = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val query = GetDailyQuestLeaderboardQuery(targetDate, limit)
        val result = dailyQuestUseCase.getDailyQuestLeaderboard(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "일일 퀘스트 리더보드를 조회했습니다."
            )
        )
    }

    /**
     * 전체 사용자 완료 분포 조회
     * GET /api/v1/daily-quests/completion-distribution?date={date}
     */
    @GetMapping("/completion-distribution")
    fun getGlobalCompletionDistribution(
        @RequestParam(required = false) date: String?
    ): ResponseEntity<ApiResponse<Any>> {
        val targetDate = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val query = GetGlobalCompletionDistributionQuery(targetDate)
        val result = dailyQuestUseCase.getGlobalCompletionDistribution(query)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "전체 사용자 완료 분포를 조회했습니다."
            )
        )
    }

    /**
     * 최고 연속 완벽한 하루 달성자 TOP 10
     * GET /api/v1/daily-quests/top-consecutive-perfect
     */
    @GetMapping("/top-consecutive-perfect")
    fun getTopConsecutivePerfectDaysUsers(): ResponseEntity<ApiResponse<Any>> {
        val result = dailyQuestUseCase.getTopConsecutivePerfectDaysUsers(10)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result.map { (userId, days) ->
                    mapOf(
                        "userId" to userId,
                        "consecutivePerfectDays" to days,
                        "title" to when {
                            days >= 100 -> "🏆 레전드"
                            days >= 50 -> "💎 마스터"
                            days >= 30 -> "🥇 챔피언"
                            days >= 14 -> "🥈 전문가"
                            days >= 7 -> "🥉 숙련자"
                            else -> "🔥 도전자"
                        }
                    )
                },
                message = "연속 완벽한 하루 TOP 10을 조회했습니다."
            )
        )
    }

    // ===============================================
    // 5. 게임화 요소 API
    // ===============================================

    /**
     * 게임화된 상태 메시지 조회
     * GET /api/v1/daily-quests/status-message?userId={userId}&date={date}
     */
    @GetMapping("/status-message")
    fun getGameifiedStatusMessage(
        @RequestParam userId: Long,
        @RequestParam(required = false) date: String?
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val targetDate = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val result = dailyQuestUseCase.getGameifiedStatusMessage(userIdVO, targetDate)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = mapOf("statusMessage" to result),
                message = "상태 메시지를 조회했습니다."
            )
        )
    }

    /**
     * 다음 달성 가능한 마일스톤 정보 조회
     * GET /api/v1/daily-quests/next-milestone?userId={userId}&date={date}
     */
    @GetMapping("/next-milestone")
    fun getNextMilestone(
        @RequestParam userId: Long,
        @RequestParam(required = false) date: String?
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val targetDate = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val result = dailyQuestUseCase.getNextMilestone(userIdVO, targetDate)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result ?: mapOf("message" to "모든 마일스톤을 달성했습니다! 🎉"),
                message = "다음 마일스톤 정보를 조회했습니다."
            )
        )
    }

    /**
     * 사용자 성취도 분석
     * GET /api/v1/daily-quests/achievement-analysis?userId={userId}&days={days}
     */
    @GetMapping("/achievement-analysis")
    fun analyzeUserAchievement(
        @RequestParam userId: Long,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val result = dailyQuestUseCase.analyzeUserAchievement(userIdVO, days)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "최근 ${days}일간의 성취도를 분석했습니다."
            )
        )
    }

    // ===============================================
    // 6. 관리자 기능 API (퀘스트 템플릿 관리)
    // ===============================================

    /**
     * 모든 일일 퀘스트 템플릿 조회
     * GET /api/v1/daily-quests/templates
     */
    @GetMapping("/templates")
    fun getAllDailyQuestTemplates(): ResponseEntity<ApiResponse<Any>> {
        val result = dailyQuestUseCase.getAllDailyQuestTemplates()
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "일일 퀘스트 템플릿을 조회했습니다."
            )
        )
    }

    /**
     * 특수 보상 통계 조회
     * GET /api/v1/daily-quests/special-reward-stats?userId={userId}
     */
    @GetMapping("/special-reward-stats")
    fun getSpecialRewardStats(@RequestParam userId: Long): ResponseEntity<ApiResponse<Any>> {
        val userIdVO = UserId(userId.toString())
        val result = dailyQuestUseCase.getSpecialRewardStats(userIdVO)
        
        return ResponseEntity.ok(
            ApiResponse.success(
                data = result,
                message = "특수 보상 통계를 조회했습니다."
            )
        )
    }

    // ===============================================
    // 7. 헬스체크 및 유틸리티 API
    // ===============================================

    /**
     * 일일 퀘스트 시스템 상태 확인
     * GET /api/v1/daily-quests/health
     */
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                data = mapOf(
                    "status" to "healthy",
                    "timestamp" to LocalDate.now(),
                    "message" to "🎮 삶을 게임처럼 즐겨라! 일일 퀘스트 시스템이 정상 작동 중입니다."
                ),
                message = "일일 퀘스트 시스템이 정상 작동 중입니다."
            )
        )
    }
}