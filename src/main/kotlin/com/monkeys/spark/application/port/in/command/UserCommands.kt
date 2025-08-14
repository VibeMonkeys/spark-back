package com.monkeys.spark.application.port.`in`.command

/**
 * 사용자 관련 Command 객체들
 */

/**
 * 사용자 생성 커맨드
 */
data class CreateUserCommand(
    val email: String,
    val password: String,
    val name: String,
    val avatarUrl: String
)

/**
 * 프로필 업데이트 커맨드
 */
data class UpdateProfileCommand(
    val userId: String,
    val name: String?,
    val avatarUrl: String?
)

/**
 * 선호도 업데이트 커맨드
 */
data class UpdatePreferencesCommand(
    val userId: String,
    val preferences: Map<String, Boolean>
)