package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

/**
 * 사용자 생성 요청 DTO
 */
data class CreateUserRequest(
    val email: String,
    val password: String,
    val name: String,
    val avatarUrl: String
)

/**
 * 프로필 업데이트 요청 DTO
 */
data class UpdateProfileRequest(
    val name: String?,
    val bio: String?,
    val avatarUrl: String?
)

/**
 * 선호도 업데이트 요청 DTO
 */
data class UpdatePreferencesRequest(
    val preferences: Map<String, Boolean>
)