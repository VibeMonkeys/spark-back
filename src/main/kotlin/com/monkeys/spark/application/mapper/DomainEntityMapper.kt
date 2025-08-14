package com.monkeys.spark.application.mapper

import com.monkeys.spark.domain.model.*
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.*
import com.monkeys.spark.domain.vo.story.*
import com.monkeys.spark.domain.vo.reward.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component

@Component
class DomainEntityMapper {
    
    private val objectMapper = ObjectMapper()
    
    /**
     * User Domain → UserEntity 변환
     */
    fun toEntity(user: User): UserEntity {
        val entity = UserEntity()
        entity.id = user.id.value
        entity.email = user.email.value
        entity.name = user.name.value
        entity.avatarUrl = user.avatarUrl.value
        entity.level = user.level.value
        entity.levelTitle = user.levelTitle.name
        entity.currentPoints = user.currentPoints.value
        entity.totalPoints = user.totalPoints.value
        entity.currentStreak = user.currentStreak.value
        entity.longestStreak = user.longestStreak.value
        entity.completedMissions = user.completedMissions
        entity.totalDays = user.totalDays
        entity.preferences = objectMapper.writeValueAsString(
            user.preferences.mapKeys { it.key.name }
        )
        entity.thisMonthPoints = user.statistics.thisMonthPoints.value
        entity.thisMonthMissions = user.statistics.thisMonthMissions
        entity.averageRating = user.statistics.averageRating.value
        entity.totalRatings = user.statistics.totalRatings
        entity.categoryStats = objectMapper.writeValueAsString(
            user.statistics.categoryStats.mapKeys { it.key.name }
                .mapValues { mapOf(
                    "completed" to it.value.completed,
                    "total" to it.value.total
                )}
        )
        entity.createdAt = user.createdAt
        entity.updatedAt = user.updatedAt
        return entity
    }
    
    /**
     * UserEntity → User Domain 변환
     */
    fun toDomain(entity: UserEntity): User {
        val preferences = try {
            val prefsMap: Map<String, Boolean> = objectMapper.readValue(entity.preferences)
            prefsMap.mapKeys { MissionCategory.valueOf(it.key) }.toMutableMap()
        } catch (e: Exception) {
            MissionCategory.values().associateWith { true }.toMutableMap()
        }
        
        val categoryStats = try {
            val statsMap: Map<String, Map<String, Int>> = objectMapper.readValue(entity.categoryStats)
            statsMap.mapKeys { MissionCategory.valueOf(it.key) }
                .mapValues { CategoryStat(
                    completed = it.value["completed"] ?: 0,
                    total = it.value["total"] ?: 0
                )}
                .toMutableMap()
        } catch (e: Exception) {
            MissionCategory.values().associateWith { CategoryStat() }.toMutableMap()
        }
        
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
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * Mission Domain → MissionEntity 변환
     */
    fun toEntity(mission: Mission): MissionEntity {
        val entity = MissionEntity()
        entity.id = mission.id.value
        entity.userId = mission.userId.value
        entity.title = mission.title.value
        entity.description = mission.description.value
        entity.detailedDescription = mission.detailedDescription.value
        entity.category = mission.category.name
        entity.difficulty = mission.difficulty.name
        entity.status = mission.status.name
        entity.rewardPoints = mission.rewardPoints.value
        entity.estimatedMinutes = mission.estimatedMinutes
        entity.imageUrl = mission.imageUrl.value
        entity.tips = objectMapper.writeValueAsString(mission.tips)
        entity.conditions = objectMapper.writeValueAsString(mission.conditions)
        entity.progress = mission.progress
        entity.completedBy = mission.statistics.completedBy
        entity.averageRating = mission.statistics.averageRating.value
        entity.totalRatings = mission.statistics.totalRatings
        entity.averageCompletionTime = mission.statistics.averageCompletionTime
        entity.popularityScore = mission.statistics.popularityScore
        entity.assignedAt = mission.assignedAt
        entity.startedAt = mission.startedAt
        entity.completedAt = mission.completedAt
        entity.expiresAt = mission.expiresAt
        entity.createdAt = mission.createdAt
        entity.updatedAt = mission.updatedAt
        return entity
    }
    
    /**
     * MissionEntity → Mission Domain 변환
     */
    fun toDomain(entity: MissionEntity): Mission {
        val tips = try {
            objectMapper.readValue<List<String>>(entity.tips)
        } catch (e: Exception) {
            emptyList<String>()
        }.toMutableList()
        
        val conditions = try {
            objectMapper.readValue<MissionConditions>(entity.conditions)
        } catch (e: Exception) {
            MissionConditions()
        }
        
        val statistics = MissionStatistics(
            completedBy = entity.completedBy,
            averageRating = Rating(entity.averageRating),
            totalRatings = entity.totalRatings,
            averageCompletionTime = entity.averageCompletionTime,
            popularityScore = entity.popularityScore
        )
        
        return Mission(
            id = MissionId(entity.id),
            userId = UserId(entity.userId),
            title = MissionTitle(entity.title),
            description = MissionDescription(entity.description),
            detailedDescription = MissionDescription(entity.detailedDescription),
            category = MissionCategory.valueOf(entity.category),
            difficulty = MissionDifficulty.valueOf(entity.difficulty),
            status = MissionStatus.valueOf(entity.status),
            rewardPoints = Points(entity.rewardPoints),
            estimatedMinutes = entity.estimatedMinutes,
            imageUrl = ImageUrl(entity.imageUrl),
            tips = tips,
            conditions = conditions,
            statistics = statistics,
            progress = entity.progress,
            assignedAt = entity.assignedAt,
            startedAt = entity.startedAt,
            completedAt = entity.completedAt,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * Story Domain → StoryEntity 변환
     */
    fun toEntity(story: Story): StoryEntity {
        val entity = StoryEntity()
        entity.id = story.id.value
        entity.userId = story.userId.value
        entity.missionId = story.missionId.value
        entity.missionTitle = story.missionTitle.value
        entity.missionCategory = story.missionCategory.name
        entity.storyText = story.storyText.value
        entity.images = objectMapper.writeValueAsString(story.images.map { it.value })
        entity.location = story.location.value
        entity.autoTags = objectMapper.writeValueAsString(story.autoTags.map { it.value })
        entity.userTags = objectMapper.writeValueAsString(story.userTags.map { it.value })
        entity.isPublic = story.isPublic
        entity.likes = story.likes.value
        entity.comments = story.comments.value
        entity.createdAt = story.createdAt
        entity.updatedAt = story.updatedAt
        return entity
    }
    
    /**
     * StoryEntity → Story Domain 변환
     */
    fun toDomain(entity: StoryEntity): Story {
        val images = try {
            val imageUrls: List<String> = objectMapper.readValue(entity.images)
            imageUrls.map { ImageUrl(it) }.toMutableList()
        } catch (e: Exception) {
            mutableListOf<ImageUrl>()
        }
        
        val autoTags = try {
            val tagStrings: List<String> = objectMapper.readValue(entity.autoTags)
            tagStrings.map { HashTag(it) }.toMutableList()
        } catch (e: Exception) {
            mutableListOf<HashTag>()
        }
        
        val userTags = try {
            val tagStrings: List<String> = objectMapper.readValue(entity.userTags)
            tagStrings.map { HashTag(it) }.toMutableList()
        } catch (e: Exception) {
            mutableListOf<HashTag>()
        }
        
        return Story(
            id = StoryId(entity.id),
            userId = UserId(entity.userId),
            missionId = MissionId(entity.missionId),
            missionTitle = MissionTitle(entity.missionTitle),
            missionCategory = MissionCategory.valueOf(entity.missionCategory),
            storyText = StoryText(entity.storyText),
            images = images,
            location = Location(entity.location),
            autoTags = autoTags,
            userTags = userTags,
            isPublic = entity.isPublic,
            likes = LikeCount(entity.likes),
            comments = CommentCount(entity.comments),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * StoryComment Domain → StoryCommentEntity 변환
     */
    fun toEntity(comment: StoryComment): StoryCommentEntity {
        val entity = StoryCommentEntity()
        entity.id = comment.id
        entity.storyId = comment.storyId.value
        entity.userId = comment.userId.value
        entity.userName = comment.userName.value
        entity.userAvatarUrl = comment.userAvatarUrl.value
        entity.content = comment.content
        entity.createdAt = comment.createdAt
        return entity
    }
    
    /**
     * StoryCommentEntity → StoryComment Domain 변환
     */
    fun toDomain(entity: StoryCommentEntity): StoryComment {
        return StoryComment(
            id = entity.id,
            storyId = StoryId(entity.storyId),
            userId = UserId(entity.userId),
            userName = UserName(entity.userName),
            userAvatarUrl = AvatarUrl(entity.userAvatarUrl),
            content = entity.content,
            createdAt = entity.createdAt
        )
    }
    
    /**
     * Reward Domain → RewardEntity 변환
     */
    fun toEntity(reward: Reward): RewardEntity {
        val entity = RewardEntity()
        entity.id = reward.id.value
        entity.title = reward.title.value
        entity.description = reward.description.value
        entity.category = reward.category.name
        entity.brand = reward.brand.value
        entity.originalPrice = reward.originalPrice.value
        entity.requiredPoints = reward.requiredPoints.value
        entity.discountPercentage = reward.discountPercentage.value
        entity.imageUrl = reward.imageUrl.value
        entity.expirationDays = reward.expirationDays.value
        entity.isPopular = reward.isPopular
        entity.isPremium = reward.isPremium
        entity.isActive = reward.isActive
        entity.totalExchanged = reward.totalExchanged
        entity.createdAt = reward.createdAt
        entity.updatedAt = reward.updatedAt
        return entity
    }
    
    /**
     * RewardEntity → Reward Domain 변환
     */
    fun toDomain(entity: RewardEntity): Reward {
        return Reward(
            id = RewardId(entity.id),
            title = RewardTitle(entity.title),
            description = RewardDescription(entity.description),
            category = RewardCategory.valueOf(entity.category),
            brand = BrandName(entity.brand),
            originalPrice = OriginalPrice(entity.originalPrice),
            requiredPoints = Points(entity.requiredPoints),
            discountPercentage = DiscountPercentage(entity.discountPercentage),
            imageUrl = ImageUrl(entity.imageUrl),
            expirationDays = ExpirationDays(entity.expirationDays),
            isPopular = entity.isPopular,
            isPremium = entity.isPremium,
            isActive = entity.isActive,
            totalExchanged = entity.totalExchanged,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    /**
     * UserReward Domain → UserRewardEntity 변환
     */
    fun toEntity(userReward: UserReward): UserRewardEntity {
        val entity = UserRewardEntity()
        entity.id = userReward.id
        entity.userId = userReward.userId.value
        entity.rewardId = userReward.rewardId.value
        entity.rewardTitle = userReward.rewardTitle.value
        entity.rewardBrand = userReward.rewardBrand.value
        entity.pointsUsed = userReward.pointsUsed.value
        entity.exchangeCode = userReward.exchangeCode
        entity.status = userReward.status.name
        entity.expiresAt = userReward.expiresAt
        entity.usedAt = userReward.usedAt
        entity.createdAt = userReward.createdAt
        return entity
    }
    
    /**
     * UserRewardEntity → UserReward Domain 변환
     */
    fun toDomain(entity: UserRewardEntity): UserReward {
        return UserReward(
            id = entity.id,
            userId = UserId(entity.userId),
            rewardId = RewardId(entity.rewardId),
            rewardTitle = RewardTitle(entity.rewardTitle),
            rewardBrand = BrandName(entity.rewardBrand),
            pointsUsed = Points(entity.pointsUsed),
            exchangeCode = entity.exchangeCode,
            status = RewardStatus.valueOf(entity.status),
            expiresAt = entity.expiresAt,
            usedAt = entity.usedAt,
            createdAt = entity.createdAt
        )
    }
    
    /**
     * StoryLike Entity 생성
     */
    fun createStoryLikeEntity(storyId: StoryId, userId: UserId): StoryLikeEntity {
        val entity = StoryLikeEntity()
        entity.storyId = storyId.value
        entity.userId = userId.value
        return entity
    }
}