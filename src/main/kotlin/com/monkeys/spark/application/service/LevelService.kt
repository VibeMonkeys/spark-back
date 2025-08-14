package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.LevelUseCase
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.LevelInfo
import com.monkeys.spark.domain.model.LevelSystem
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.stereotype.Service

@Service
class LevelService(
    private val userRepository: UserRepository
) : LevelUseCase {
    
    override fun getUserLevelProgress(userId: String): User {
        return userRepository.findById(UserId(userId))
            ?: throw IllegalArgumentException("User not found: $userId")
    }
    
    override fun getLevelSystem(userId: String): User {
        return userRepository.findById(UserId(userId))
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