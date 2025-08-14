package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*

/**
 * 인증 응답 DTO
 */
data class AuthResponse(
    val user: UserResponse,
    val token: String,
    val refreshToken: String
)