package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.UserUseCase
import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.application.port.`in`.command.UpdatePreferencesCommand
import com.monkeys.spark.application.port.`in`.command.UpdateProfileCommand
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.model.UserStatistics
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.user.AvatarUrl
import com.monkeys.spark.domain.vo.user.Email
import com.monkeys.spark.domain.vo.user.UserName
import com.monkeys.spark.infrastructure.adapter.out.persistence.UserPersistenceAdapter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserApplicationService(
    private val userRepository: UserRepository,
    private val userPersistenceAdapter: UserPersistenceAdapter
) : UserUseCase {

    override fun createUser(command: CreateUserCommand): User {
        // 이메일 중복 확인
        val email = Email(command.email)
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists: ${command.email}")
        }

        val user = User.create(
            email = email,
            name = UserName(command.name),
            avatarUrl = AvatarUrl(command.avatarUrl)
        )

        return userPersistenceAdapter.saveWithPassword(user, command.password)
    }

    @Transactional(readOnly = true)
    override fun getUser(userId: UserId): User? {
        return userRepository.findById(userId)
    }

    @Transactional(readOnly = true)
    override fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(Email(email))
    }

    override fun updateProfile(command: UpdateProfileCommand): User {
        val userId = UserId(command.userId)
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: ${command.userId}")

        command.name?.let {
            user.name = UserName(it)
        }

        command.avatarUrl?.let {
            user.avatarUrl = AvatarUrl(it)
        }

        return userRepository.save(user)
    }

    override fun updatePreferences(command: UpdatePreferencesCommand): User {
        val userId = UserId(command.userId)
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: ${command.userId}")

        val preferences = command.preferences.mapKeys { (key, _) ->
            MissionCategory.valueOf(key.uppercase())
        }

        user.updatePreferences(preferences)
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun getUserStatistics(userId: UserId): UserStatistics {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        return user.statistics
    }

    @Transactional(readOnly = true)
    override fun getLeaderboard(limit: Int): List<User> {
        return userRepository.findTopUsersByThisMonthPoints(limit)
    }

    @Transactional(readOnly = true)
    override fun getStreakLeaderboard(limit: Int): List<User> {
        return userRepository.findTopUsersByStreak(limit)
    }

    /**
     * 사용자 포인트 획득 (미션 완료 시 호출)
     */
    fun addPoints(userId: UserId, points: Points): User {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        user.earnPoints(points)
        return userRepository.save(user)
    }

    /**
     * 사용자 포인트 차감 (리워드 교환 시 호출)
     */
    fun deductPoints(userId: UserId, points: Points): User {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        user.spendPoints(points)
        return userRepository.save(user)
    }

    /**
     * 연속 수행일 증가
     */
    fun incrementStreak(userId: UserId): User {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        user.incrementStreak()
        return userRepository.save(user)
    }

    /**
     * 완료된 미션 수 증가
     */
    fun incrementCompletedMissions(userId: UserId): User {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        user.completedMissions++
        return userRepository.save(user)
    }

    /**
     * 미션 완료 처리 (포인트, 연속일, 완료수, 통계 모두 업데이트)
     */
    fun completeMission(userId: UserId, mission: Mission): User {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        user.completeMission(mission)
        return userRepository.save(user)
    }

    /**
     * 연속 수행일 리셋
     */
    fun resetStreak(userId: UserId): User {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        user.resetStreak()
        return userRepository.save(user)
    }

    /**
     * 사용자가 존재하는지 확인
     */
    @Transactional(readOnly = true)
    fun existsById(userId: UserId): Boolean {
        return userRepository.existsById(userId)
    }
}