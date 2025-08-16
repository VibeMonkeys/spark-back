package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.UserUseCase
import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.domain.exception.InvalidCredentialsException
import com.monkeys.spark.domain.exception.UserAlreadyExistsException
import com.monkeys.spark.domain.exception.UserNotFoundException
import com.monkeys.spark.domain.exception.ValidationException
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AuthResult
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.RefreshTokenJpaRepository
import com.monkeys.spark.infrastructure.config.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AuthApplicationService(
    private val userUseCase: UserUseCase,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
    private val refreshTokenRepository: RefreshTokenJpaRepository
) {

    fun register(email: String, password: String, name: String, avatarUrl: String?): AuthResult {
        // 이메일 중복 체크
        val existingUser = userUseCase.getUserByEmail(email)
        if (existingUser != null) {
            throw UserAlreadyExistsException(email)
        }

        // 사용자 생성 (UserApplicationService에서 비밀번호 해시 처리)
        val command = CreateUserCommand(
            email = email,
            password = password, // 원본 비밀번호 전달
            name = name,
            avatarUrl = avatarUrl ?: generateDefaultAvatarUrl(name)
        )
        val user = userUseCase.createUser(command)

        // JWT 토큰 생성
        return generateTokens(user)
    }

    fun login(email: String, password: String): AuthResult {
        try {
            // Spring Security를 통한 인증
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password)
            )

            // 인증된 사용자 정보 조회 (username은 실제로는 userId)
            val userId = authentication.name
            val user = userUseCase.getUser(UserId(userId))
                ?: throw UserNotFoundException("unknown")

            // JWT 토큰 생성
            return generateTokens(user)
        } catch (e: Exception) {
            throw InvalidCredentialsException()
        }
    }

    fun refreshToken(refreshToken: String): AuthResult {
        // Refresh token 유효성 검증
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw ValidationException("유효하지 않은 refresh token입니다.")
        }

        // 데이터베이스에서 refresh token 조회
        val tokenEntity = refreshTokenRepository.findByTokenAndIsActive(refreshToken, true)
            ?: throw ValidationException("refresh token을 찾을 수 없습니다.")

        if (tokenEntity.isExpired()) {
            throw ValidationException("만료된 refresh token입니다.")
        }

        // 사용자 조회
        val user = userUseCase.getUser(UserId(tokenEntity.userId))
            ?: throw UserNotFoundException("unknown")

        // 새 토큰 생성
        return generateTokens(user)
    }

    fun logout(refreshToken: String) {
        // Refresh token 무효화
        refreshTokenRepository.revokeTokenByToken(refreshToken)
    }

    fun revokeAllUserTokens(userId: String) {
        refreshTokenRepository.revokeAllTokensByUserId(userId)
    }

    private fun generateTokens(user: User): AuthResult {
        val accessToken = jwtUtil.generateAccessToken(user.id.value)
        val refreshToken = jwtUtil.generateRefreshToken(user.id.value)

        // Refresh token을 데이터베이스에 저장
        val refreshTokenEntity = RefreshTokenEntity(
            userId = user.id.value,
            token = refreshToken,
            expiresAt = LocalDateTime.now().plusDays(7), // 7일
            createdAt = LocalDateTime.now()
        )
        refreshTokenRepository.save(refreshTokenEntity)

        return AuthResult(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun generateDefaultAvatarUrl(name: String): String {
        val hash = name.hashCode().toString().takeLast(3)
        return "https://images.unsplash.com/photo-150700321116$hash?w=150&h=150&fit=crop&crop=face"
    }
}

