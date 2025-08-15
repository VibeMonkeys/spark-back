package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*
import java.time.LocalDateTime

// Mission Domain Aggregate Root
data class Mission(
    var id: MissionId,
    var userId: UserId,
    var title: MissionTitle,
    var description: MissionDescription,
    var detailedDescription: MissionDescription,
    var category: MissionCategory,
    var difficulty: MissionDifficulty,
    var status: MissionStatus = MissionStatus.ASSIGNED,
    var rewardPoints: Points,
    var estimatedMinutes: Int,
    var imageUrl: ImageUrl,
    var tips: MutableList<String> = mutableListOf(),
    var conditions: MissionConditions = MissionConditions(),
    var statistics: MissionStatistics = MissionStatistics(),
    var progress: Int = 0, // 0-100 percentage
    var isTemplate: Boolean = false, // 템플릿 미션 여부
    var assignedAt: LocalDateTime = LocalDateTime.now(),
    var startedAt: LocalDateTime? = null,
    var completedAt: LocalDateTime? = null,
    var expiresAt: LocalDateTime = LocalDateTime.now().plusDays(1),
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            userId: UserId,
            title: String,
            description: String,
            detailedDescription: String,
            category: MissionCategory,
            difficulty: MissionDifficulty,
            imageUrl: String,
            tips: List<String> = emptyList(),
            conditions: MissionConditions = MissionConditions()
        ): Mission {
            return Mission(
                id = MissionId.generate(),
                userId = userId,
                title = MissionTitle(title),
                description = MissionDescription(description),
                detailedDescription = MissionDescription(detailedDescription),
                category = category,
                difficulty = difficulty,
                rewardPoints = Points(difficulty.basePoints),
                estimatedMinutes = difficulty.estimatedMinutes,
                imageUrl = ImageUrl(imageUrl),
                tips = tips.toMutableList(),
                conditions = conditions
            )
        }
        
        fun createSample(
            id: MissionId,
            userId: UserId,
            title: String,
            description: String,
            category: MissionCategory,
            difficulty: MissionDifficulty,
            rewardPoints: Int
        ): Mission {
            return Mission(
                id = id,
                userId = userId,
                title = MissionTitle(title),
                description = MissionDescription(description),
                detailedDescription = MissionDescription(description),
                category = category,
                difficulty = difficulty,
                rewardPoints = Points(rewardPoints),
                estimatedMinutes = difficulty.estimatedMinutes,
                imageUrl = ImageUrl("https://images.unsplash.com/photo-1584515501397-335d595b2a17?w=400"),
                tips = mutableListOf(),
                conditions = MissionConditions()
            )
        }
    }
    
    fun start(): Mission {
        require(status == MissionStatus.ASSIGNED) { "Mission must be in ASSIGNED status to start" }
        require(!isExpired()) { "Cannot start expired mission" }
        
        status = MissionStatus.IN_PROGRESS
        startedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun updateProgress(newProgress: Int): Mission {
        require(status == MissionStatus.IN_PROGRESS) { "Mission must be in progress to update progress" }
        require(newProgress in 0..100) { "Progress must be between 0 and 100" }
        
        progress = newProgress
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun complete(): Mission {
        require(status == MissionStatus.IN_PROGRESS || status == MissionStatus.ASSIGNED) { 
            "Mission must be assigned or in progress to complete" 
        }
        require(!isExpired()) { "Cannot complete expired mission" }
        
        // ASSIGNED 상태에서 바로 완료하는 경우 시작 시간도 설정
        if (status == MissionStatus.ASSIGNED) {
            startedAt = LocalDateTime.now()
        }
        
        status = MissionStatus.COMPLETED
        progress = 100
        completedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        statistics.incrementCompletedCount()
        return this
    }
    
    fun fail(): Mission {
        require(status == MissionStatus.IN_PROGRESS) { "Mission must be in progress to fail" }
        
        status = MissionStatus.FAILED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun expire(): Mission {
        require(status != MissionStatus.COMPLETED) { "Cannot expire completed mission" }
        
        status = MissionStatus.EXPIRED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
    
    fun isActive(): Boolean = status in listOf(MissionStatus.ASSIGNED, MissionStatus.IN_PROGRESS) && !isExpired()
    
    fun addTip(tip: String): Mission {
        tips.add(tip)
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun updateConditions(newConditions: MissionConditions): Mission {
        conditions = newConditions
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun getTimeLeft(): String {
        if (isExpired()) return "만료됨"
        
        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(now, expiresAt)
        
        return when {
            duration.toHours() > 0 -> "${duration.toHours()}시간 ${duration.toMinutes() % 60}분"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}분"
            else -> "곧 만료"
        }
    }
    
    fun calculateFinalPoints(): Points {
        return when (status) {
            MissionStatus.COMPLETED -> {
                // Bonus points for streak or speed completion could be added here
                var finalPoints = rewardPoints.value
                
                // Streak bonus (example: 10% bonus for every 5 streak days)
                // This would need access to user's current streak
                
                // Time bonus (example: complete within first 6 hours gets 20% bonus)
                startedAt?.let { start ->
                    completedAt?.let { complete ->
                        val completionHours = java.time.Duration.between(start, complete).toHours()
                        if (completionHours <= 6) {
                            finalPoints = (finalPoints * 1.2).toInt()
                        }
                    }
                }
                
                Points(finalPoints)
            }
            else -> Points(0)
        }
    }
}