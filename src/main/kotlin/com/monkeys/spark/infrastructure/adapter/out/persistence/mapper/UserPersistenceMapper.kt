package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.model.UserStatistics
import com.monkeys.spark.domain.model.CategoryStat
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component

@Component
class UserPersistenceMapper(
    private val objectMapper: ObjectMapper
) {
    
    fun toEntity(domain: User): UserEntity {
        val entity = UserEntity()
        entity.id = domain.id.value
        entity.email = domain.email.value
        entity.password = domain.password
        entity.name = domain.name.value
        entity.avatarUrl = domain.avatarUrl.value
        entity.level = domain.level.value
        entity.levelTitle = domain.levelTitle.name
        entity.currentPoints = domain.currentPoints.value
        entity.totalPoints = domain.totalPoints.value
        entity.currentStreak = domain.currentStreak.value
        entity.longestStreak = domain.longestStreak.value
        entity.completedMissions = domain.completedMissions
        entity.totalDays = domain.totalDays
        
        // Convert preferences map to JSON
        entity.preferences = objectMapper.writeValueAsString(
            domain.preferences.mapKeys { it.key.name }
        )
        
        // Convert statistics
        entity.thisMonthPoints = domain.statistics.thisMonthPoints.value
        entity.thisMonthMissions = domain.statistics.thisMonthMissions
        entity.averageRating = domain.statistics.averageRating.value
        entity.totalRatings = domain.statistics.totalRatings
        
        // Convert category stats to JSON
        entity.categoryStats = objectMapper.writeValueAsString(
            domain.statistics.categoryStats.mapKeys { it.key.name }
                .mapValues { mapOf(
                    "completed" to it.value.completed,
                    "total" to it.value.total
                )}
        )
        
        entity.lastCompletedDate = domain.lastCompletedDate
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        
        return entity
    }
    
    fun toDomain(entity: UserEntity): User {
        // Parse preferences from JSON
        val preferencesMap: Map<String, Boolean> = try {
            objectMapper.readValue(entity.preferences)
        } catch (e: Exception) {
            emptyMap()
        }
        val preferences = preferencesMap.mapNotNull { (key, value) ->
            try {
                MissionCategory.valueOf(key) to value
            } catch (e: IllegalArgumentException) {
                println("⚠️ [UserPersistenceMapper] Invalid preference category: $key, skipping...")
                null
            }
        }.toMap().toMutableMap()
        
        // Parse category stats from JSON
        val categoryStatsMap: Map<String, Map<String, Int>> = try {
            objectMapper.readValue(entity.categoryStats)
        } catch (e: Exception) {
            emptyMap()
        }
        val categoryStats = categoryStatsMap.mapNotNull { (key, value) ->
            try {
                MissionCategory.valueOf(key) to CategoryStat(
                    completed = value["completed"] ?: 0,
                    total = value["total"] ?: 0
                )
            } catch (e: IllegalArgumentException) {
                println("⚠️ [UserPersistenceMapper] Invalid category stats key: $key, skipping...")
                null
            }
        }.toMap().toMutableMap()
        
        val statistics = UserStatistics(
            categoryStats = categoryStats,
            thisMonthPoints = Points(entity.thisMonthPoints),
            thisMonthMissions = entity.thisMonthMissions,
            averageRating = Rating(entity.averageRating),
            totalRatings = entity.totalRatings
        )
        
        return User(
            id = UserId(entity.id),
            email = Email(entity.email),
            password = entity.password,
            name = UserName(entity.name),
            avatarUrl = AvatarUrl(entity.avatarUrl),
            level = Level(entity.level),
            levelTitle = UserLevelTitle.valueOf(entity.levelTitle),
            currentPoints = Points(entity.currentPoints),
            totalPoints = Points(entity.totalPoints),
            currentStreak = Streak(entity.currentStreak),
            longestStreak = Streak(entity.longestStreak),
            completedMissions = entity.completedMissions,
            totalDays = entity.totalDays,
            preferences = preferences,
            statistics = statistics,
            lastCompletedDate = entity.lastCompletedDate,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}