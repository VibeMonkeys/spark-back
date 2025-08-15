package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

/**
 * 스탯 포인트 할당 요청 DTO
 */
data class AllocateStatPointsRequest(
    val statType: String, // STRENGTH, INTELLIGENCE, CREATIVITY, SOCIABILITY, ADVENTUROUS, DISCIPLINE
    val points: Int
)