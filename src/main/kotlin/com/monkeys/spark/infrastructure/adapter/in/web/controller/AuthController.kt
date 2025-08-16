package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.service.AuthService
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.LoginRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.RefreshTokenRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.SignupRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.AuthResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val responseMapper: ResponseMapper
) {

    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResult = authService.register(
            email = request.email,
            password = request.password,
            name = request.name,
            avatarUrl = request.avatarUrl
        )

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
        val authResult = authService.login(
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
        authService.logout(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.success("logout_success", "로그아웃이 완료되었습니다."))
    }

    /**
     * 토큰 갱신
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResult = authService.refreshToken(request.refreshToken)

        val userResponse = responseMapper.toUserResponse(authResult.user)
        val authResponse = AuthResponse(
            user = userResponse,
            token = authResult.accessToken,
            refreshToken = authResult.refreshToken
        )

        return ResponseEntity.ok(ApiResponse.success(authResponse, "토큰이 갱신되었습니다."))
    }

}

