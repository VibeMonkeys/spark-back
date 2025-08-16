package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.AuthUseCase
import com.monkeys.spark.application.port.`in`.UserUseCase
import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.config.JwtUtil
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.LoginRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.RefreshTokenRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.SignupRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AuthResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val responseMapper: ResponseMapper,
    private val jwtUtil: JwtUtil
) {

    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val command = CreateUserCommand(
            email = request.email,
            password = request.password,
            name = request.name,
            avatarUrl = request.avatarUrl
        )
        val authResult = authUseCase.register(command)

        val userResponse = responseMapper.toUserResponse(authResult.user)
        val authResponse = AuthResponse(
            user = userResponse,
            token = authResult.accessToken,
            refreshToken = authResult.refreshToken
        )

        return ResponseEntity.ok(ApiResponse.success(authResponse, "회원가입이 완료되었습니다."))
    }

    /**
     * 로그인
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResult = authUseCase.login(
            email = request.email,
            password = request.password
        )

        val userResponse = responseMapper.toUserResponse(authResult.user)
        val authResponse = AuthResponse(
            user = userResponse,
            token = authResult.accessToken,
            refreshToken = authResult.refreshToken
        )

        return ResponseEntity.ok(ApiResponse.success(authResponse, "로그인이 완료되었습니다."))
    }

    /**
     * 로그아웃
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<String>> {
        authUseCase.logout(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.success("logout_success", "로그아웃이 완료되었습니다."))
    }

    /**
     * 토큰 갱신
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResult = authUseCase.refreshToken(request.refreshToken)

        val userResponse = responseMapper.toUserResponse(authResult.user)
        val authResponse = AuthResponse(
            user = userResponse,
            token = authResult.accessToken,
            refreshToken = authResult.refreshToken
        )

        return ResponseEntity.ok(ApiResponse.success(authResponse, "토큰이 갱신되었습니다."))
    }

    /**
     * 데모 로그인 (개발용)
     * POST /api/v1/auth/demo-login/{userId}
     */
    @PostMapping("/demo-login/{userId}")
    fun demoLogin(@PathVariable userId: Long): ResponseEntity<ApiResponse<AuthResponse>> {
        // 데모 계정인지 확인 (5, 6, 7번 사용자만 허용)
        if (userId !in listOf(5L, 6L, 7L)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("INVALID_DEMO_USER", "유효하지 않은 데모 사용자입니다.")
            )
        }

        try {
            // 사용자 정보 조회
            val user = userUseCase.getUser(UserId(userId))
            
            // JWT 토큰 생성
            val accessToken = jwtUtil.generateAccessToken(userId.toString())
            val refreshToken = jwtUtil.generateRefreshToken(userId.toString())

            val userResponse = responseMapper.toUserResponse(user)
            val authResponse = AuthResponse(
                user = userResponse,
                token = accessToken,
                refreshToken = refreshToken
            )

            return ResponseEntity.ok(ApiResponse.success(authResponse, "데모 로그인이 완료되었습니다."))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("DEMO_LOGIN_FAILED", "데모 로그인에 실패했습니다: ${e.message}")
            )
        }
    }

}

