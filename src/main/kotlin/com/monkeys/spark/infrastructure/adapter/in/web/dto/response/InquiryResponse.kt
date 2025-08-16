package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import java.time.LocalDateTime

data class InquiryResponse(
    val id: String,
    val userId: String?,
    val email: String,
    val subject: String,
    val message: String,
    val status: String,
    val statusDisplay: String,
    val response: String?,
    val respondedAt: LocalDateTime?,
    val respondedBy: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)