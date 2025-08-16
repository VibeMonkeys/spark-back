package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.LevelUseCase
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.LevelInfoResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.LevelSystemResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.UserLevelProgressResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/levels")
class LevelController(
    private val levelUseCase: LevelUseCase,
    private val responseMapper: ResponseMapper
) {

    /**
     * 전체 레벨 시스템 조회
     * GET /api/v1/levels/system?user_id={userId}
     */
    @GetMapping("/system")
    fun getLevelSystem(
        @RequestParam("user_id") userId: Long
    ): ResponseEntity<ApiResponse<LevelSystemResponse>> {
        val user = levelUseCase.getLevelSystem(UserId(userId))
        val levelSystemResponse = responseMapper.toLevelSystemResponse(user)
        return ResponseEntity.ok(ApiResponse.success(levelSystemResponse))
    }

    /**
     * 사용자 레벨 진행 상황 조회
     * GET /api/v1/levels/progress?user_id={userId}
     */
    @GetMapping("/progress")
    fun getUserLevelProgress(
        @RequestParam("user_id") userId: Long
    ): ResponseEntity<ApiResponse<UserLevelProgressResponse>> {
        val user = levelUseCase.getUserLevelProgress(UserId(userId))
        val progressResponse = responseMapper.toUserLevelProgressResponse(user)
        return ResponseEntity.ok(ApiResponse.success(progressResponse))
    }

    /**
     * 특정 레벨 정보 조회
     * GET /api/v1/levels/{level}
     */
    @GetMapping("/{level}")
    fun getLevelInfo(
        @PathVariable level: Int
    ): ResponseEntity<ApiResponse<LevelInfoResponse>> {
        val levelInfo = levelUseCase.getLevelInfo(level)
            ?: return ResponseEntity.ok(
                ApiResponse.error<LevelInfoResponse>(
                    "레벨 정보를 찾을 수 없습니다.", 
                    "LEVEL_NOT_FOUND"
                )
            )

        val levelInfoResponse = responseMapper.toLevelInfoResponse(levelInfo)
        return ResponseEntity.ok(ApiResponse.success(levelInfoResponse))
    }

    /**
     * 모든 레벨 정보 조회
     * GET /api/v1/levels/all
     */
    @GetMapping("/all")
    fun getAllLevels(): ResponseEntity<ApiResponse<List<LevelInfoResponse>>> {
        val allLevels = levelUseCase.getAllLevels()
        val levelResponses = allLevels.map { responseMapper.toLevelInfoResponse(it) }
        return ResponseEntity.ok(ApiResponse.success(levelResponses))
    }
}