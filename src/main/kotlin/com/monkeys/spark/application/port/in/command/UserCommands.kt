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
    val avatarUrl: String?
)

/**
 * 프로필 업데이트 커맨드
 */
data class UpdateProfileCommand(
    val userId: Long,
    val name: String?,
    val bio: String?,
    val avatarUrl: String?
)

/**
 * 선호도 업데이트 커맨드
 */
data class UpdatePreferencesCommand(
    val userId: Long,
    val preferences: Map<String, Boolean>
)

/**
 * 비밀번호 변경 커맨드
 */
data class ChangePasswordCommand(
    val userId: Long,
    val currentPassword: String,
    val newPassword: String
)