package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

/**
 * 회원가입 요청 DTO
 */
data class SignupRequest(
    val email: String,
    val password: String,
    val name: String,
    val avatarUrl: String? = null
)

/**
 * 로그인 요청 DTO
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * 토큰 갱신 요청 DTO
 */
data class RefreshTokenRequest(
    val refreshToken: String
)