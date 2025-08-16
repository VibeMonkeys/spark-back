package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.UserUseCase
import com.monkeys.spark.application.port.`in`.command.ChangePasswordCommand
import com.monkeys.spark.application.port.`in`.command.CreateUserCommand
import com.monkeys.spark.application.port.`in`.command.UpdatePreferencesCommand
import com.monkeys.spark.application.port.`in`.command.UpdateProfileCommand
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.exception.InvalidPasswordException
import com.monkeys.spark.domain.exception.UserAlreadyExistsException
import com.monkeys.spark.domain.exception.UserNotFoundException
import com.monkeys.spark.domain.exception.WeakPasswordException
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.model.UserStatistics
import com.monkeys.spark.domain.service.UserPasswordDomainService
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.user.AvatarUrl
import com.monkeys.spark.domain.vo.user.Email
import com.monkeys.spark.domain.vo.user.UserName
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserApplicationService(
    private val userRepository: UserRepository,
    private val userPasswordDomainService: UserPasswordDomainService
) : UserUseCase {

    override fun createUser(command: CreateUserCommand): User {
        // 이메일 중복 확인
        val email = Email(command.email)
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException(command.email)
        }

        val user = User.create(
            email = email,
            password = command.password,
            name = UserName(command.name),
            avatarUrl = AvatarUrl(command.avatarUrl ?: "")
        )

        return userRepository.saveWithPassword(user, command.password)
    }

    @Transactional(readOnly = true)
    override fun getUser(userId: UserId): User {
        return userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())
    }

    @Transactional(readOnly = true)
    override fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(Email(email))
    }

    override fun updateProfile(command: UpdateProfileCommand): User {
        val userId = UserId(command.userId)
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        val newName = command.name?.let { UserName(it) }
        val newAvatarUrl = command.avatarUrl?.let { AvatarUrl(it) }

        user.updateProfile(
            newName = newName,
            newBio = command.bio,
            newAvatarUrl = newAvatarUrl
        )

        return userRepository.save(user)
    }

    override fun changePassword(command: ChangePasswordCommand): User {
        val userId = UserId(command.userId)
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        // 도메인 서비스를 통한 비밀번호 변경 처리
        val updatedUser = try {
            userPasswordDomainService.changePassword(user, command.currentPassword, command.newPassword)
        } catch (e: IllegalArgumentException) {
            when {
                e.message?.contains("Current password") == true -> throw InvalidPasswordException()
                e.message?.contains("security requirements") == true -> throw WeakPasswordException()
                else -> throw InvalidPasswordException()
            }
        }

        return userRepository.save(updatedUser)
    }

    override fun updatePreferences(command: UpdatePreferencesCommand): User {
        val userId = UserId(command.userId)
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        val preferences = command.preferences.mapKeys { (key, _) ->
            MissionCategory.valueOf(key.uppercase())
        }

        user.updatePreferences(preferences)
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun getUserStatistics(userId: UserId): UserStatistics {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

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
            ?: throw UserNotFoundException(userId.value.toString())

        user.earnPoints(points)
        return userRepository.save(user)
    }

    /**
     * 사용자 포인트 차감 (리워드 교환 시 호출)
     */
    fun deductPoints(userId: UserId, points: Points): User {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        user.spendPoints(points)
        return userRepository.save(user)
    }

    /**
     * 연속 수행일 증가
     */
    fun incrementStreak(userId: UserId): User {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        user.incrementStreak()
        return userRepository.save(user)
    }

    /**
     * 완료된 미션 수 증가
     */
    fun incrementCompletedMissions(userId: UserId): User {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        // 도메인 모델을 통한 적절한 방법으로 수정 필요
        // 직접 필드 접근 대신 도메인 메서드 사용
        val mission = createDummyMissionForIncrement() // 임시 구현
        user.completeMission(mission)
        return userRepository.save(user)
    }

    private fun createDummyMissionForIncrement(): Mission {
        // 실제로는 이 메서드가 호출되지 않도록 리팩토링 필요
        // MissionApplicationService에서 직접 user.completeMission()을 호출해야 함
        return Mission.createSample(
            id = com.monkeys.spark.domain.vo.common.MissionId.generate(),
            userId = com.monkeys.spark.domain.vo.common.UserId(0L),
            title = "Dummy Mission",
            description = "Dummy",
            category = com.monkeys.spark.domain.vo.mission.MissionCategory.LEARNING,
            difficulty = com.monkeys.spark.domain.vo.mission.MissionDifficulty.EASY,
            rewardPoints = 0
        )
    }

    /**
     * 미션 완료 처리 (포인트, 연속일, 완료수, 통계 모두 업데이트)
     */
    fun completeMission(userId: UserId, mission: Mission): User {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        user.completeMission(mission)
        return userRepository.save(user)
    }

    /**
     * 연속 수행일 리셋
     */
    fun resetStreak(userId: UserId): User {
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

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