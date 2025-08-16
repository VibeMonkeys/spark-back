package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.UserUseCase
import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.application.port.`in`.command.UpdatePreferencesCommand
import com.monkeys.spark.application.port.`in`.command.UpdateProfileCommand
import com.monkeys.spark.application.port.`in`.command.ChangePasswordCommand
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.CreateUserRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.UpdatePreferencesRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.UpdateProfileRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.ChangePasswordRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userUseCase: UserUseCase,
    private val responseMapper: ResponseMapper
) {


    /**
     * 사용자 조회
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userUseCase.getUser(UserId(userId))
        val response = responseMapper.toUserResponse(user)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 프로필 페이지 데이터 조회
     * GET /api/v1/users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    fun getProfilePage(@PathVariable userId: String): ResponseEntity<ApiResponse<ProfilePageResponse>> {
        val user = userUseCase.getUser(UserId(userId))
        val userStatistics = userUseCase.getUserStatistics(UserId(userId))

        // TODO: 실제 업적 및 최근 미션 데이터 조회
        val achievements = listOf(
            ProfileAchievementResponse("1", "첫 걸음", "첫 미션 완료", "🎯", true),
            ProfileAchievementResponse("2", "불타는 열정", "7일 연속 미션 완료", "🔥", true),
            ProfileAchievementResponse("3", "소셜 버터플라이", "사교적 미션 10개 완료", "🦋", true),
            ProfileAchievementResponse("4", "모험왕", "모험적 미션 15개 완료", "🗺️", true),
            ProfileAchievementResponse("5", "마라토너", "30일 연속 미션 완료", "🏃", false),
            ProfileAchievementResponse("6", "마스터", "레벨 10 달성", "👑", false)
        )

        val recentMissions = listOf(
            RecentMissionResponse("1", "새로운 카페 발견하기", "사교적", "오늘", 20, "cafe-image-url"),
            RecentMissionResponse("2", "가보지 않은 길로 퇴근하기", "모험적", "어제", 20, "adventure-image-url")
        )

        val response = ProfilePageResponse(
            user = responseMapper.toUserResponse(user),
            statistics = responseMapper.toUserStatisticsResponse(userStatistics),
            achievements = achievements,
            recentMissions = recentMissions
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 프로필 업데이트
     * PUT /api/v1/users/{userId}/profile
     */
    @PutMapping("/{userId}/profile")
    fun updateUserProfile(
        @PathVariable userId: String,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val command = UpdateProfileCommand(
            userId = userId,
            name = request.name,
            bio = request.bio,
            avatarUrl = request.avatarUrl
        )
        val user = userUseCase.updateProfile(command)
        val response = responseMapper.toUserResponse(user)

        return ResponseEntity.ok(ApiResponse.success(response, "프로필이 업데이트되었습니다."))
    }

    /**
     * 비밀번호 변경
     * POST /api/v1/users/{userId}/change-password  
     */
    @PostMapping("/{userId}/change-password")
    fun changePassword(
        @PathVariable userId: String,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<String>> {
        val command = ChangePasswordCommand(
            userId = userId,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword
        )
        userUseCase.changePassword(command)
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."))
    }

    /**
     * 선호도 업데이트
     * PUT /api/v1/users/{userId}/preferences
     */
    @PutMapping("/{userId}/preferences")
    fun updatePreferences(
        @PathVariable userId: String,
        @RequestBody request: UpdatePreferencesRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val command = UpdatePreferencesCommand(
            userId = userId,
            preferences = request.preferences
        )
        val user = userUseCase.updatePreferences(command)
        val response = responseMapper.toUserResponse(user)

        return ResponseEntity.ok(ApiResponse.success(response, "선호도가 업데이트되었습니다."))
    }

    /**
     * 사용자 통계 조회
     * GET /api/v1/users/{userId}/statistics
     */
    @GetMapping("/{userId}/statistics")
    fun getUserStatistics(@PathVariable userId: String): ResponseEntity<ApiResponse<UserStatisticsResponse>> {
        val statistics = userUseCase.getUserStatistics(UserId(userId))
        val response = responseMapper.toUserStatisticsResponse(statistics)

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 리더보드 조회 (포인트 기준)
     * GET /api/v1/users/leaderboard?limit={limit}
     */
    @GetMapping("/leaderboard")
    fun getLeaderboard(
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<LeaderboardUserResponse>>> {
        val users = userUseCase.getLeaderboard(limit)
        val response = users.mapIndexed { index, user ->
            LeaderboardUserResponse(
                rank = index + 1,
                userId = user.id.value,
                name = user.name.value,
                avatarUrl = user.avatarUrl.value,
                level = user.level.value,
                levelTitle = user.levelTitle.displayName,
                points = user.currentPoints.value,
                streak = user.currentStreak.value
            )
        }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 연속 수행일 리더보드 조회
     * GET /api/v1/users/streak-leaderboard?limit={limit}
     */
    @GetMapping("/streak-leaderboard")
    fun getStreakLeaderboard(
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<LeaderboardUserResponse>>> {
        val users = userUseCase.getStreakLeaderboard(limit)
        val response = users.mapIndexed { index, user ->
            LeaderboardUserResponse(
                rank = index + 1,
                userId = user.id.value,
                name = user.name.value,
                avatarUrl = user.avatarUrl.value,
                level = user.level.value,
                levelTitle = user.levelTitle.displayName,
                points = user.currentPoints.value,
                streak = user.currentStreak.value
            )
        }

        return ResponseEntity.ok(ApiResponse.success(response))
    }
}

