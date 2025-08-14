package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import java.time.LocalDateTime

// User Domain Aggregate Root
data class User(
    var id: UserId,
    var email: Email,
    var name: UserName,
    var avatarUrl: AvatarUrl,
    var level: Level = Level(1),
    var levelTitle: UserLevelTitle = UserLevelTitle.BEGINNER,
    var currentPoints: Points = Points(0),
    var totalPoints: Points = Points(0),
    var currentStreak: Streak = Streak(0),
    var longestStreak: Streak = Streak(0),
    var completedMissions: Int = 0,
    var totalDays: Int = 0,
    var preferences: MutableMap<MissionCategory, Boolean> = mutableMapOf(),
    var statistics: UserStatistics = UserStatistics(),
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            email: Email,
            name: UserName,
            avatarUrl: AvatarUrl
        ): User {
            return User(
                id = UserId.generate(),
                email = email,
                name = name,
                avatarUrl = avatarUrl,
                preferences = MissionCategory.values().associateWith { true }.toMutableMap()
            )
        }
    }
    
    fun earnPoints(points: Points): User {
        currentPoints += points
        totalPoints += points
        updatedAt = LocalDateTime.now()
        checkLevelUp()
        return this
    }
    
    fun spendPoints(points: Points): User {
        require(currentPoints >= points) { "Insufficient points" }
        currentPoints -= points
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun incrementStreak(): User {
        currentStreak = currentStreak.increment()
        if (currentStreak.value > longestStreak.value) {
            longestStreak = currentStreak
        }
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun resetStreak(): User {
        currentStreak = currentStreak.reset()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun completeMission(mission: Mission): User {
        completedMissions++
        earnPoints(mission.rewardPoints)
        incrementStreak()
        statistics.incrementCategoryCount(mission.category)
        return this
    }
    
    fun updatePreferences(newPreferences: Map<MissionCategory, Boolean>): User {
        preferences.putAll(newPreferences)
        updatedAt = LocalDateTime.now()
        return this
    }
    
    private fun checkLevelUp() {
        val newLevel = calculateLevelFromPoints(totalPoints.value)
        if (newLevel > level.value) {
            level = Level(newLevel)
            levelTitle = calculateLevelTitle(newLevel)
        }
    }
    
    private fun calculateLevelFromPoints(totalPoints: Int): Int {
        return when {
            totalPoints < 500 -> 1
            totalPoints < 1500 -> 2
            totalPoints < 3000 -> 3
            totalPoints < 5000 -> 4
            totalPoints < 8000 -> 5
            totalPoints < 12000 -> 6
            totalPoints < 17000 -> 7
            totalPoints < 23000 -> 8
            totalPoints < 30000 -> 9
            totalPoints < 38000 -> 10
            totalPoints < 47000 -> 11
            totalPoints < 57000 -> 12
            else -> ((totalPoints - 57000) / 12000) + 13
        }
    }
    
    private fun calculateLevelTitle(level: Int): UserLevelTitle {
        return when (level) {
            1, 2 -> UserLevelTitle.BEGINNER
            in 3..5 -> UserLevelTitle.EXPLORER
            in 6..8 -> UserLevelTitle.ADVENTURER
            in 9..12 -> UserLevelTitle.EXPERT
            in 13..20 -> UserLevelTitle.MASTER
            else -> UserLevelTitle.LEGEND
        }
    }
}