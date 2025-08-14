package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.*
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = ["http://localhost:3001", "http://localhost:3002", "http://localhost:5173"])
class AuthController(
    private val userUseCase: UserUseCase,
    private val responseMapper: ResponseMapper
) {

    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        try {
            // 이메일 중복 체크
            val existingUser = userUseCase.getUserByEmail(request.email)
            if (existingUser != null) {
                return ResponseEntity.ok(ApiResponse.error("이미 사용 중인 이메일입니다.", "EMAIL_ALREADY_EXISTS"))
            }

            val command = CreateUserCommand(
                email = request.email,
                name = request.name,
                avatarUrl = request.avatarUrl ?: generateDefaultAvatarUrl(request.name)
            )
            val user = userUseCase.createUser(command)
            val userResponse = responseMapper.toUserResponse(user)

            // 실제로는 JWT 토큰을 생성해야 하지만, 현재는 간단히 사용자 정보만 반환
            val authResponse = AuthResponse(
                user = userResponse,
                token = "temp_token_${user.id.value}", // 임시 토큰
                refreshToken = "temp_refresh_${user.id.value}"
            )

            return ResponseEntity.ok(ApiResponse.success(authResponse, "회원가입이 완료되었습니다."))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.ok(ApiResponse.error(e.message ?: "회원가입에 실패했습니다.", "SIGNUP_FAILED"))
        } catch (e: Exception) {
            return ResponseEntity.ok(ApiResponse.error("회원가입 중 오류가 발생했습니다: ${e.message}", "SIGNUP_ERROR"))
        }
    }

    /**
     * 로그인
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        try {
            val user = userUseCase.getUserByEmail(request.email)
                ?: return ResponseEntity.ok(ApiResponse.error("존재하지 않는 이메일입니다.", "USER_NOT_FOUND"))

            // 실제로는 비밀번호 검증을 해야 하지만, 현재는 간단히 이메일만 체크
            // TODO: 비밀번호 해싱 및 검증 로직 추가 필요
            
            val userResponse = responseMapper.toUserResponse(user)
            val authResponse = AuthResponse(
                user = userResponse,
                token = "temp_token_${user.id.value}", // 임시 토큰
                refreshToken = "temp_refresh_${user.id.value}"
            )

            return ResponseEntity.ok(ApiResponse.success(authResponse, "로그인이 완료되었습니다."))
        } catch (e: Exception) {
            return ResponseEntity.ok(ApiResponse.error("로그인에 실패했습니다.", "LOGIN_FAILED"))
        }
    }

    /**
     * 로그아웃
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String?): ResponseEntity<ApiResponse<String>> {
        // 실제로는 토큰 무효화 로직이 필요하지만, 현재는 간단히 성공 응답만 반환
        return ResponseEntity.ok(ApiResponse.success("logout_success", "로그아웃이 완료되었습니다."))
    }

    /**
     * 토큰 갱신
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        // 실제로는 refresh token 검증 후 새 토큰 발급
        // 현재는 임시로 기존 토큰 정보 반환
        return try {
            val userId = request.refreshToken.removePrefix("temp_refresh_")
            val user = userUseCase.getUser(UserId(userId))
                ?: return ResponseEntity.ok(ApiResponse.error("유효하지 않은 토큰입니다.", "INVALID_TOKEN"))

            val userResponse = responseMapper.toUserResponse(user)
            val authResponse = AuthResponse(
                user = userResponse,
                token = "temp_token_${user.id.value}",
                refreshToken = "temp_refresh_${user.id.value}"
            )

            ResponseEntity.ok(ApiResponse.success(authResponse, "토큰이 갱신되었습니다."))
        } catch (e: Exception) {
            ResponseEntity.ok(ApiResponse.error("토큰 갱신에 실패했습니다.", "REFRESH_FAILED"))
        }
    }

    /**
     * 기본 아바타 URL 생성
     */
    private fun generateDefaultAvatarUrl(name: String): String {
        val hash = name.hashCode().toString().takeLast(3)
        return "https://images.unsplash.com/photo-150700321116$hash?w=150&h=150&fit=crop&crop=face"
    }
}

