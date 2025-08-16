package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.model.UserStatistics
import com.monkeys.spark.domain.vo.common.UserId

/**
 * 사용자 관련 UseCase 인터페이스
 */
interface UserUseCase {

    /**
     * 사용자 생성
     */
    fun createUser(command: CreateUserCommand): User

    /**
     * 사용자 조회
     */
    fun getUser(userId: UserId): User

    /**
     * 이메일로 사용자 조회
     */
    fun getUserByEmail(email: String): User?

    /**
     * 사용자 프로필 업데이트
     */
    fun updateProfile(command: UpdateProfileCommand): User

    /**
     * 사용자 비밀번호 변경
     */
    fun changePassword(command: ChangePasswordCommand): User

    /**
     * 사용자 선호도 업데이트
     */
    fun updatePreferences(command: UpdatePreferencesCommand): User

    /**
     * 사용자 통계 조회
     */
    fun getUserStatistics(userId: UserId): UserStatistics

    /**
     * 레더보드 조회 (포인트 기준)
     */
    fun getLeaderboard(limit: Int): List<User>

    /**
     * 연속 수행일 레더보드
     */
    fun getStreakLeaderboard(limit: Int): List<User>
}