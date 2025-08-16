package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.AchievementUseCase
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AchievementCountResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AchievementRarityResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AchievementResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 업적 시스템 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/achievements")
class AchievementController(
    private val achievementUseCase: AchievementUseCase
) {

    /**
     * 사용자의 모든 업적 조회 (달성된 것과 진행 중인 것 포함)
     */
    @GetMapping
    fun getUserAchievements(
        authentication: Authentication
    ): ResponseEntity<ApiResponse<List<AchievementResponse>>> {
        val userId = UserId(authentication.name.toLong())
        val achievements = achievementUseCase.getUserAchievements(userId)

        val response = achievements.map { achievement ->
            AchievementResponse(
                id = achievement.achievementType.id,
                name = achievement.achievementType.displayName,
                description = achievement.achievementType.description,
                icon = achievement.achievementType.icon,
                color = achievement.achievementType.color,
                category = achievement.achievementType.category.displayName,
                rarity = AchievementRarityResponse(
                    name = achievement.achievementType.rarity.displayName,
                    color = achievement.achievementType.rarity.color,
                    order = achievement.achievementType.rarity.order
                ),
                progress = achievement.progress,
                isUnlocked = achievement.isUnlocked(),
                unlockedAt = if (achievement.isUnlocked()) achievement.unlockedAt.toString() else null
            )
        }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 사용자의 달성한 업적 개수 조회
     */
    @GetMapping("/count")
    fun getUserAchievementCount(
        authentication: Authentication
    ): ResponseEntity<ApiResponse<AchievementCountResponse>> {
        val userId = UserId(authentication.name.toLong())
        val count = achievementUseCase.getUserAchievementCount(userId)

        val response = AchievementCountResponse(
            unlockedCount = count,
            totalCount = 12 // AchievementType enum의 총 개수
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 업적 통계 조회 (관리자용)
     */
    @GetMapping("/statistics")
    fun getAchievementStatistics(): ResponseEntity<ApiResponse<Map<String, Int>>> {
        val statistics = achievementUseCase.getAchievementStatistics()
        return ResponseEntity.ok(ApiResponse.success(statistics))
    }

}