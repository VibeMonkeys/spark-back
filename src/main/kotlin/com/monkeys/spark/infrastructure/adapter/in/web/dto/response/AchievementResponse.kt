package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 업적 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AchievementResponse(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val color: String,
    val category: String,
    val rarity: AchievementRarityResponse,
    val progress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: String?
)

/**
 * 업적 희귀도 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AchievementRarityResponse(
    val name: String,
    val color: String,
    val order: Int
)

/**
 * 업적 개수 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AchievementCountResponse(
    val unlockedCount: Int,
    val totalCount: Int
)