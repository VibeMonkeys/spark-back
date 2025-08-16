package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AuthResult

/**
 * 인증 관련 UseCase 인터페이스
 */
interface AuthUseCase {

    /**
     * 회원가입
     */
    fun register(command: CreateUserCommand): AuthResult

    /**
     * 로그인
     */
    fun login(email: String, password: String): AuthResult

    /**
     * 토큰 갱신
     */
    fun refreshToken(refreshToken: String): AuthResult

    /**
     * 로그아웃
     */
    fun logout(refreshToken: String)

    /**
     * 사용자의 모든 토큰 무효화
     */
    fun revokeAllUserTokens(userId: String)
}