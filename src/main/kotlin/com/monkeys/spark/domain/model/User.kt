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
        statistics.addThisMonthPoints(mission.rewardPoints)
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
        val newLevelInfo = UserLevelInfo.fromPoints(totalPoints)
        if (newLevelInfo.isLevelUp(level)) {
            level = newLevelInfo.level
            levelTitle = newLevelInfo.levelTitle
        }
    }
    
}