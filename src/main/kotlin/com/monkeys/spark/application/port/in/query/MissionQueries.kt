package com.monkeys.spark.application.port.`in`.query

import java.time.LocalDateTime

/**
 * 미션 관련 Query 객체들
 */

/**
 * 완료된 미션 조회 쿼리
 */
data class CompletedMissionsQuery(
    val userId: Long,
    val page: Int = 0,
    val size: Int = 20,
    val category: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)