package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.LevelUseCase
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
        @RequestParam("user_id") userId: String
    ): ResponseEntity<LevelSystemResponse> {
        return try {
            val user = levelUseCase.getLevelSystem(userId)
            val levelSystemResponse = responseMapper.toLevelSystemResponse(user)
            ResponseEntity.ok(levelSystemResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
    
    /**
     * 사용자 레벨 진행 상황 조회
     * GET /api/v1/levels/progress?user_id={userId}
     */
    @GetMapping("/progress")
    fun getUserLevelProgress(
        @RequestParam("user_id") userId: String
    ): ResponseEntity<UserLevelProgressResponse> {
        return try {
            val user = levelUseCase.getUserLevelProgress(userId)
            val progressResponse = responseMapper.toUserLevelProgressResponse(user)
            ResponseEntity.ok(progressResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
    
    /**
     * 특정 레벨 정보 조회
     * GET /api/v1/levels/{level}
     */
    @GetMapping("/{level}")
    fun getLevelInfo(
        @PathVariable level: Int
    ): ResponseEntity<LevelInfoResponse> {
        return try {
            val levelInfo = levelUseCase.getLevelInfo(level)
                ?: return ResponseEntity.notFound().build()
            
            val levelInfoResponse = responseMapper.toLevelInfoResponse(levelInfo)
            ResponseEntity.ok(levelInfoResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
    
    /**
     * 모든 레벨 정보 조회
     * GET /api/v1/levels/all
     */
    @GetMapping("/all")
    fun getAllLevels(): ResponseEntity<List<LevelInfoResponse>> {
        return try {
            val allLevels = levelUseCase.getAllLevels()
            val levelResponses = allLevels.map { responseMapper.toLevelInfoResponse(it) }
            ResponseEntity.ok(levelResponses)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}