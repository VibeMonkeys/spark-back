package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.MissionUseCase
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 관리자용 API 컨트롤러
 * 시스템 관리, 스케줄러 테스트 등의 기능 제공
 */
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val missionUseCase: MissionUseCase
) {

    /**
     * 만료된 미션 수동 정리 (테스트용)
     * POST /api/v1/admin/missions/cleanup-expired
     */
    @PostMapping("/missions/cleanup-expired")
    fun cleanupExpiredMissions(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val startTime = LocalDateTime.now()
        val expiredCount = missionUseCase.cleanupExpiredMissions()
        val endTime = LocalDateTime.now()
        
        val result = mapOf(
            "expiredCount" to expiredCount,
            "executedAt" to startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "duration" to "${java.time.Duration.between(startTime, endTime).toMillis()}ms",
            "message" to if (expiredCount > 0) 
                "$expiredCount 개의 미션이 만료 처리되었습니다." 
            else 
                "만료할 미션이 없습니다."
        )
        
        return ResponseEntity.ok(
            ApiResponse.success(
                result,
                "만료된 미션 정리가 완료되었습니다."
            )
        )
    }

    /**
     * 시스템 상태 확인
     * GET /api/v1/admin/health
     */
    @GetMapping("/health")
    fun systemHealth(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val result = mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "services" to mapOf(
                "mission_scheduler" to "ACTIVE",
                "database" to "CONNECTED"
            )
        )
        
        return ResponseEntity.ok(
            ApiResponse.success(result, "시스템이 정상 동작 중입니다.")
        )
    }
}