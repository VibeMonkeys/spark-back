package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.AuthUseCase
import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.application.port.out.RefreshTokenRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.exception.InvalidCredentialsException
import com.monkeys.spark.domain.exception.UserAlreadyExistsException
import com.monkeys.spark.domain.exception.UserNotFoundException
import com.monkeys.spark.domain.exception.ValidationException
import com.monkeys.spark.domain.exception.InvalidDemoUserException
import com.monkeys.spark.domain.model.RefreshToken
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.user.AvatarUrl
import com.monkeys.spark.domain.vo.user.Email
import com.monkeys.spark.domain.vo.user.UserName
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AuthResult
import com.monkeys.spark.infrastructure.config.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AuthApplicationService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) : AuthUseCase {

    override fun register(command: CreateUserCommand): AuthResult {
        // 이메일 중복 체크
        val existingUser = userRepository.findByEmail(Email(command.email))
        if (existingUser != null) {
            throw UserAlreadyExistsException(command.email)
        }

        // 사용자 생성 (비밀번호는 빈 문자열로 초기화)
        val user = User.create(
            email = Email(command.email),
            password = "", // 비밀번호는 repository에서 해싱하여 설정
            name = UserName(command.name),
            avatarUrl = AvatarUrl(command.avatarUrl ?: generateDefaultAvatarUrl(command.name))
        )

        val savedUser = userRepository.saveWithPassword(user, command.password)

        // JWT 토큰 생성
        return generateTokens(savedUser)
    }

    override fun login(email: String, password: String): AuthResult {
        try {
            // Spring Security를 통한 인증
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password)
            )

            // 인증된 사용자 정보 조회 (username은 실제로는 userId)
            val userId = authentication.name
            val user = userRepository.findById(UserId(userId.toLong()))
                ?: throw UserNotFoundException("unknown")

            // JWT 토큰 생성
            return generateTokens(user)
        } catch (e: Exception) {
            throw InvalidCredentialsException()
        }
    }

    override fun refreshToken(refreshToken: String): AuthResult {
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
        val user = userRepository.findById(tokenEntity.userId)
            ?: throw UserNotFoundException("unknown")

        // 새 토큰 생성
        return generateTokens(user)
    }

    override fun logout(refreshToken: String) {
        // Refresh token 무효화
        refreshTokenRepository.revokeTokenByToken(refreshToken)
    }

    override fun revokeAllUserTokens(userId: String) {
        refreshTokenRepository.revokeAllTokensByUserId(userId)
    }

    private fun generateTokens(user: User): AuthResult {
        val accessToken = jwtUtil.generateAccessToken(user.id.value.toString())
        val refreshTokenStr = jwtUtil.generateRefreshToken(user.id.value.toString())

        // Refresh token을 데이터베이스에 저장
        val refreshToken = RefreshToken.create(
            userId = user.id,
            token = refreshTokenStr,
            expiresAt = LocalDateTime.now().plusDays(7) // 7일
        )
        refreshTokenRepository.save(refreshToken)

        return AuthResult(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshTokenStr
        )
    }

    override fun demoLogin(userId: Long): AuthResult {
        // 데모 계정인지 확인 (1, 2, 3번 사용자만 허용)
        if (userId !in listOf(1L, 2L, 3L)) {
            throw InvalidDemoUserException("유효하지 않은 데모 사용자입니다.")
        }

        // 사용자 정보 조회
        val user = userRepository.findById(UserId(userId)) 
            ?: throw UserNotFoundException("사용자를 찾을 수 없습니다.")
        
        // JWT 토큰 생성
        val accessToken = jwtUtil.generateAccessToken(userId.toString())
        val refreshTokenStr = jwtUtil.generateRefreshToken(userId.toString())

        // 리프레시 토큰 저장
        val refreshToken = RefreshToken.create(
            userId = user.id,
            token = refreshTokenStr,
            expiresAt = LocalDateTime.now().plusDays(7)
        )
        refreshTokenRepository.save(refreshToken)

        return AuthResult(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshTokenStr
        )
    }

    private fun generateDefaultAvatarUrl(name: String): String {
        val hash = name.hashCode().toString().takeLast(3)
        return "https://images.unsplash.com/photo-150700321116$hash?w=150&h=150&fit=crop&crop=face"
    }

}

