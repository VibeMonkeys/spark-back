package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.LevelUseCase
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.service.LevelSystem
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.user.LevelInfo
import org.springframework.stereotype.Service

@Service
class LevelApplicationService(
    private val userRepository: UserRepository
) : LevelUseCase {

    override fun getUserLevelProgress(userId: UserId): User {
        return userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")
    }

    override fun getLevelSystem(userId: UserId): User {
        return userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")
    }

    override fun getLevelInfo(level: Int): LevelInfo? {
        if (level < 1 || level > 21) {
            throw IllegalArgumentException("Invalid level: $level. Level must be between 1 and 21.")
        }
        return LevelSystem.getLevelInfo(level)
    }

    override fun getAllLevels(): List<LevelInfo> {
        return LevelSystem.getAllLevels()
    }

}