package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.UserStatsUseCase
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.stat.StatType
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.AllocateStatPointsRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * 사용자 스탯 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/stats")
class UserStatsController(
    private val userStatsUseCase: UserStatsUseCase
) {

    /**
     * 사용자 스탯 조회
     * GET /api/v1/stats
     */
    @GetMapping
    fun getUserStats(authentication: Authentication): ResponseEntity<ApiResponse<UserStatsResponse>> {
        val userId = UserId(authentication.name)
        val userStats = userStatsUseCase.getUserStats(userId)
        val response = UserStatsResponse.from(userStats)
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 스탯 포인트 할당
     * POST /api/v1/stats/allocate
     */
    @PostMapping("/allocate")
    fun allocateStatPoints(
        @RequestBody request: AllocateStatPointsRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<UserStatsResponse>> {
        val userId = UserId(authentication.name)
        
        val statType = try {
            StatType.valueOf(request.statType.uppercase())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("유효하지 않은 스탯 타입입니다", "INVALID_STAT_TYPE")
            )
        }
        
        val updatedStats = userStatsUseCase.allocateStatPoints(userId, statType, request.points)
        val response = UserStatsResponse.from(updatedStats)
        
        return ResponseEntity.ok(ApiResponse.success(response, "스탯 포인트가 할당되었습니다"))
    }

    /**
     * 전체 스탯 랭킹 조회
     * GET /api/v1/stats/ranking/total
     */
    @GetMapping("/ranking/total")
    fun getTotalStatsRanking(
        @RequestParam(defaultValue = "100") limit: Int
    ): ResponseEntity<ApiResponse<List<UserStatsRankingResponse>>> {
        val ranking = userStatsUseCase.getTotalStatsRanking(limit)
        val response = ranking.map { UserStatsRankingResponse.from(it) }
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 특정 스탯별 랭킹 조회
     * GET /api/v1/stats/ranking/{statType}
     */
    @GetMapping("/ranking/{statType}")
    fun getStatRanking(
        @PathVariable statType: String,
        @RequestParam(defaultValue = "100") limit: Int
    ): ResponseEntity<ApiResponse<List<UserStatsRankingResponse>>> {
        val statTypeEnum = try {
            StatType.valueOf(statType.uppercase())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("유효하지 않은 스탯 타입입니다", "INVALID_STAT_TYPE")
            )
        }
        
        val ranking = userStatsUseCase.getStatRanking(statTypeEnum, limit)
        val response = ranking.map { UserStatsRankingResponse.from(it) }
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 사용자 랭킹 정보 조회
     * GET /api/v1/stats/ranking/my
     */
    @GetMapping("/ranking/my")
    fun getMyRankingInfo(authentication: Authentication): ResponseEntity<ApiResponse<UserRankingInfoResponse>> {
        val userId = UserId(authentication.name)
        val rankingInfo = userStatsUseCase.getUserRankingInfo(userId)
        val response = UserRankingInfoResponse.from(rankingInfo)
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 스탯 초기화 (개발/테스트용)
     * POST /api/v1/stats/initialize
     */
    @PostMapping("/initialize")
    fun initializeStats(authentication: Authentication): ResponseEntity<ApiResponse<UserStatsResponse>> {
        val userId = UserId(authentication.name)
        val userStats = userStatsUseCase.initializeUserStats(userId)
        val response = UserStatsResponse.from(userStats)
        
        return ResponseEntity.ok(ApiResponse.success(response, "스탯이 초기화되었습니다"))
    }
}