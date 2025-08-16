package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import java.time.LocalDateTime
import java.time.LocalDate

// User Domain Aggregate Root
data class User(
    var id: UserId,
    var email: Email,
    var password: String = "", // 암호화된 비밀번호
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
    var lastCompletedDate: LocalDateTime? = null,
    var bio: String? = null, // 한줄소개
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            email: Email,
            password: String,
            name: UserName,
            avatarUrl: AvatarUrl
        ): User {
            return User(
                id = UserId.generate(),
                email = email,
                password = password, // 암호화는 application service에서 처리
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
        val today = LocalDate.now()
        val lastCompletedLocalDate = lastCompletedDate?.toLocalDate()
        
        when {
            // 첫 완료
            lastCompletedLocalDate == null -> {
                currentStreak = Streak(1)
                lastCompletedDate = LocalDateTime.now()
            }
            // 오늘 이미 완료한 적이 있음 - streak 변경 없음
            lastCompletedLocalDate == today -> {
                // streak는 변경하지 않음
                lastCompletedDate = LocalDateTime.now()
            }
            // 어제 완료했음 - 연속 증가
            lastCompletedLocalDate == today.minusDays(1) -> {
                currentStreak = currentStreak.increment()
                lastCompletedDate = LocalDateTime.now()
            }
            // 하루 이상 공백 - streak 리셋
            else -> {
                currentStreak = Streak(1)
                lastCompletedDate = LocalDateTime.now()
            }
        }
        
        // 최장 연속 기록 갱신
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
    
    fun updateProfile(newName: UserName? = null, newBio: String? = null, newAvatarUrl: AvatarUrl? = null): User {
        if (newName != null) {
            this.name = newName
        }
        if (newBio != null) {
            this.bio = if (newBio.isBlank()) null else newBio
        }
        if (newAvatarUrl != null) {
            this.avatarUrl = newAvatarUrl
        }
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun changePassword(newPassword: String): User {
        require(newPassword.isNotBlank()) { "Password cannot be blank" }
        this.password = newPassword // 실제로는 암호화된 패스워드가 들어옴
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
        // 새로운 50레벨 시스템 포인트 기준표
        val levelPoints = mapOf(
            1 to 0, 2 to 50, 3 to 120, 4 to 200, 5 to 300,
            6 to 420, 7 to 560, 8 to 720, 9 to 900, 10 to 1100,
            11 to 1320, 12 to 1560, 13 to 1820, 14 to 2100, 15 to 2400,
            16 to 2720, 17 to 3060, 18 to 3420, 19 to 3800, 20 to 4200,
            21 to 4620, 22 to 5060, 23 to 5520, 24 to 6000, 25 to 6500,
            26 to 7020, 27 to 7560, 28 to 8120, 29 to 8700, 30 to 9300,
            31 to 9920, 32 to 10560, 33 to 11220, 34 to 11900, 35 to 12600,
            36 to 13320, 37 to 14060, 38 to 14820, 39 to 15600, 40 to 16400,
            41 to 17220, 42 to 18060, 43 to 18920, 44 to 19800, 45 to 20700,
            46 to 21620, 47 to 22560, 48 to 23520, 49 to 24500, 50 to 25500
        )
        
        return levelPoints.entries
            .sortedByDescending { it.value }
            .find { totalPoints >= it.value }
            ?.key ?: 1
    }
    
    private fun calculateLevelTitle(level: Int): UserLevelTitle {
        return when (level) {
            in 1..5 -> UserLevelTitle.BEGINNER
            in 6..10 -> UserLevelTitle.EXPLORER
            in 11..20 -> UserLevelTitle.ADVENTURER
            in 21..30 -> UserLevelTitle.EXPERT
            in 31..40 -> UserLevelTitle.MASTER
            in 41..45 -> UserLevelTitle.GRANDMASTER
            in 46..50 -> UserLevelTitle.LEGEND
            else -> UserLevelTitle.MYTHIC
        }
    }
}