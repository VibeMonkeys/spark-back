package com.monkeys.spark.application.mapper

import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.application.port.`in`.dto.HomePageData
import com.monkeys.spark.application.port.`in`.dto.UserSummary
import com.monkeys.spark.domain.model.CategoryStat
import com.monkeys.spark.domain.model.*
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.*
import com.monkeys.spark.domain.vo.story.*
import com.monkeys.spark.domain.vo.reward.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class ResponseMapper {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
    
    /**
     * User 도메인을 UserResponse로 변환
     */
    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id.value,
            name = user.name.value,
            email = user.email.value,
            avatarUrl = user.avatarUrl.value,
            level = user.level.value,
            levelTitle = user.levelTitle.displayName,
            currentPoints = user.currentPoints.value,
            totalPoints = user.totalPoints.value,
            currentStreak = user.currentStreak.value,
            longestStreak = user.longestStreak.value,
            completedMissions = user.completedMissions,
            totalDays = user.totalDays,
            joinDate = user.createdAt.format(dateFormatter),
            preferences = user.preferences.mapKeys { it.key.displayName },
            statistics = toUserStatisticsResponse(user.statistics)
        )
    }
    
    /**
     * UserStatistics 도메인을 UserStatisticsResponse로 변환
     */
    fun toUserStatisticsResponse(statistics: UserStatistics): UserStatisticsResponse {
        return UserStatisticsResponse(
            categoryStats = statistics.categoryStats.map { (category, stat) ->
                CategoryStatResponse(
                    name = category.displayName,
                    completed = stat.completed,
                    total = stat.total,
                    percentage = stat.percentage.value,
                    color = category.colorClass
                )
            },
            thisMonthPoints = statistics.thisMonthPoints.value,
            thisMonthMissions = statistics.thisMonthMissions,
            averageRating = statistics.averageRating.value
        )
    }
    
    /**
     * UserSummary를 UserSummaryResponse로 변환
     */
    fun toUserSummaryResponse(userSummary: UserSummary): UserSummaryResponse {
        val user = userSummary.user
        return UserSummaryResponse(
            name = user.name.value,
            level = user.level.value,
            levelTitle = user.levelTitle.displayName,
            currentPoints = user.currentPoints.value,
            currentStreak = user.currentStreak.value,
            progressToNextLevel = userSummary.progressToNextLevel,
            pointsToNextLevel = userSummary.pointsToNextLevel
        )
    }
    
    /**
     * Mission 도메인을 MissionResponse로 변환
     */
    fun toMissionResponse(mission: Mission): MissionResponse {
        return MissionResponse(
            id = mission.id.value,
            title = mission.title.value,
            description = mission.description.value,
            detailedDescription = mission.detailedDescription.value,
            category = mission.category.displayName,
            categoryColor = mission.category.colorClass,
            difficulty = mission.difficulty.displayName,
            status = mission.status.name,
            points = mission.rewardPoints.value,
            duration = "${mission.estimatedMinutes}분",
            image = mission.imageUrl.value,
            tips = mission.tips,
            progress = if (mission.status == MissionStatus.IN_PROGRESS) mission.progress else null,
            timeLeft = if (mission.isActive()) mission.getTimeLeft() else null,
            completedBy = generateMockCompletedBy(mission.difficulty, mission.category),
            averageRating = generateMockAverageRating(mission.difficulty, mission.category),
            assignedAt = mission.assignedAt,
            expiresAt = mission.expiresAt
        )
    }
    
    /**
     * Mission과 유사 미션들을 MissionDetailResponse로 변환
     */
    fun toMissionDetailResponse(mission: Mission, similarMissions: List<Mission>): MissionDetailResponse {
        return MissionDetailResponse(
            id = mission.id.value,
            title = mission.title.value,
            description = mission.description.value,
            detailedDescription = mission.detailedDescription.value,
            category = mission.category.displayName,
            categoryColor = mission.category.colorClass,
            difficulty = mission.difficulty.displayName,
            points = mission.rewardPoints.value,
            duration = "${mission.estimatedMinutes}분",
            image = mission.imageUrl.value,
            tips = mission.tips,
            completedBy = generateMockCompletedBy(mission.difficulty, mission.category),
            averageRating = generateMockAverageRating(mission.difficulty, mission.category),
            similarMissions = generateMockSimilarMissions(mission.category)
        )
    }
    
    /**
     * 미션 완료 시 Mission과 User 정보를 MissionCompletionResponse로 변환
     */
    fun toMissionCompletionResponse(mission: Mission, user: User, pointsEarned: Int): MissionCompletionResponse {
        // 이전 레벨 계산 (포인트 지급 전 레벨)
        val previousLevel = user.level.value
        val currentLevel = user.level.value
        val levelUp = currentLevel > previousLevel
        
        val response = MissionCompletionResponse(
            mission = toMissionResponse(mission),
            pointsEarned = pointsEarned,
            streakCount = user.currentStreak.value,
            levelUp = levelUp,
            newLevel = if (levelUp) currentLevel else null,
            totalPoints = user.totalPoints.value,
            thisMonthPoints = user.statistics.thisMonthPoints.value
        )
        
        println("🎯 [ResponseMapper] MissionCompletionResponse created: $response")
        return response
    }
    
    /**
     * Story 도메인을 StoryResponse로 변환
     */
    fun toStoryResponse(story: Story, currentUserId: String?): StoryResponse {
        // 현재 사용자가 좋아요를 눌렀는지 확인 (임시로 false 반환)
        val isLiked = false // TODO: 실제 구현에서는 StoryRepository에서 확인
        
        return StoryResponse(
            id = story.id.value,
            user = StoryUserResponse(
                name = "사용자명", // TODO: 실제 사용자 정보 조회 필요
                avatarUrl = "사용자 아바타", // TODO: 실제 사용자 정보 조회 필요
                level = "레벨 8 탐험가" // TODO: 실제 사용자 정보 조회 필요
            ),
            mission = StoryMissionResponse(
                title = story.missionTitle.value,
                category = story.missionCategory.displayName,
                categoryColor = story.missionCategory.colorClass
            ),
            story = story.storyText.value,
            images = story.images.map { it.value },
            location = story.location.value,
            tags = story.getAllTags().map { it.value },
            likes = story.likes.value,
            comments = story.comments.value,
            timeAgo = story.getTimeAgo(),
            isLiked = isLiked
        )
    }
    
    /**
     * StoryFeedItem을 StoryResponse로 변환
     */
    fun toStoryResponse(feedItem: StoryFeedItem, currentUserId: String?): StoryResponse {
        return StoryResponse(
            id = feedItem.storyId.value,
            user = StoryUserResponse(
                name = feedItem.user.name.value,
                avatarUrl = feedItem.user.avatarUrl.value,
                level = "레벨 ${feedItem.user.level.value} ${feedItem.user.levelTitle.displayName}"
            ),
            mission = StoryMissionResponse(
                title = feedItem.mission.title.value,
                category = feedItem.mission.category.displayName,
                categoryColor = feedItem.mission.category.colorClass
            ),
            story = feedItem.content.storyText.value,
            images = feedItem.content.images.map { it.value },
            location = feedItem.location.value,
            tags = feedItem.content.tags.map { it.value },
            likes = feedItem.interactions.likes.value,
            comments = feedItem.interactions.comments.value,
            timeAgo = feedItem.timeAgo,
            isLiked = feedItem.interactions.isLikedByCurrentUser
        )
    }
    
    /**
     * StoryComment를 StoryCommentResponse로 변환
     */
    fun toStoryCommentResponse(comment: StoryComment): StoryCommentResponse {
        return StoryCommentResponse(
            id = comment.id,
            userName = comment.userName.value,
            userAvatarUrl = comment.userAvatarUrl.value,
            content = comment.content,
            timeAgo = comment.getTimeAgo()
        )
    }
    
    /**
     * Reward 도메인을 RewardResponse로 변환
     */
    fun toRewardResponse(reward: Reward): RewardResponse {
        return RewardResponse(
            id = reward.id.value,
            title = reward.title.value,
            description = reward.description.value,
            category = reward.category.displayName,
            brand = reward.brand.value,
            originalPrice = reward.originalPrice.value,
            points = reward.requiredPoints.value,
            discount = reward.getDiscountText(),
            image = reward.imageUrl.value,
            expires = reward.getExpirationText(),
            popular = reward.isPopular,
            isPremium = reward.isPremium
        )
    }
    
    /**
     * UserReward를 UserRewardResponse로 변환
     */
    fun toUserRewardResponse(userReward: UserReward): UserRewardResponse {
        return UserRewardResponse(
            id = userReward.id,
            title = userReward.rewardTitle.value,
            brand = userReward.rewardBrand.value,
            points = userReward.pointsUsed.value,
            code = userReward.exchangeCode,
            status = userReward.getUsageStatusText(),
            usedAt = userReward.getUsageTimeText(),
            expiresAt = if (userReward.status == RewardStatus.AVAILABLE) 
                userReward.getTimeUntilExpiration() else null
        )
    }
    
    /**
     * UserPointsSummary를 UserPointsResponse로 변환
     */
    fun toUserPointsResponse(userPoints: UserPointsSummary): UserPointsResponse {
        return UserPointsResponse(
            current = userPoints.current,
            total = userPoints.total,
            thisMonth = userPoints.thisMonth
        )
    }
    
    /**
     * CategoryStat을 CategoryStatResponse로 변환
     */
    fun toCategoryStatResponse(category: MissionCategory, stat: CategoryStat): CategoryStatResponse {
        return CategoryStatResponse(
            name = category.displayName,
            completed = stat.completed,
            total = stat.total,
            percentage = stat.percentage.value,
            color = category.colorClass
        )
    }
    
    /**
     * 시간 형식을 "N분 전", "N시간 전" 등으로 변환
     */
    private fun formatTimeAgo(minutes: Long): String {
        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            minutes < 1440 -> "${minutes / 60}시간 전"
            else -> "${minutes / 1440}일 전"
        }
    }
    
    /**
     * 난이도에 따른 예상 시간 계산
     */
    private fun getDurationText(difficulty: MissionDifficulty): String {
        return "${difficulty.estimatedMinutes}분"
    }
    
    /**
     * 난이도와 카테고리에 따른 현실적인 미션 완료 인원 생성
     */
    private fun generateMockCompletedBy(difficulty: MissionDifficulty, category: MissionCategory): Int {
        val baseCounts = mapOf(
            MissionDifficulty.EASY to 800..1500,
            MissionDifficulty.MEDIUM to 300..700,
            MissionDifficulty.HARD to 50..200
        )
        
        val categoryMultiplier = when (category) {
            MissionCategory.ADVENTURE -> 0.8 // 모험은 상대적으로 적은 참여
            MissionCategory.HEALTH -> 1.2 // 건강은 인기 높음
            MissionCategory.SOCIAL -> 1.0 // 보통
            MissionCategory.CREATIVE -> 0.9 // 창의는 약간 적음
            MissionCategory.LEARNING -> 0.7 // 학습은 가장 적음
        }
        
        val baseRange = baseCounts[difficulty] ?: 100..500
        val min = (baseRange.first * categoryMultiplier).toInt()
        val max = (baseRange.last * categoryMultiplier).toInt()
        
        return (min..max).random()
    }
    
    /**
     * 카테고리와 난이도에 따른 현실적인 평점 생성
     */
    private fun generateMockAverageRating(difficulty: MissionDifficulty, category: MissionCategory): Double {
        val baseRating = when (difficulty) {
            MissionDifficulty.EASY -> 4.2..4.8
            MissionDifficulty.MEDIUM -> 3.8..4.5
            MissionDifficulty.HARD -> 3.5..4.2
        }
        
        val categoryAdjustment = when (category) {
            MissionCategory.ADVENTURE -> 0.2 // 모험은 만족도 높음
            MissionCategory.HEALTH -> 0.1
            MissionCategory.SOCIAL -> 0.0
            MissionCategory.CREATIVE -> 0.1
            MissionCategory.LEARNING -> -0.1 // 학습은 약간 까다로움
        }
        
        val rating = (baseRating.start + Math.random() * (baseRating.endInclusive - baseRating.start)) + categoryAdjustment
        return Math.round(rating * 10) / 10.0 // 소수점 첫째 자리까지
    }
    
    /**
     * 카테고리에 맞는 유사 미션 목록 생성
     */
    private fun generateMockSimilarMissions(category: MissionCategory): List<SimilarMissionResponse> {
        val similarMissionsByCategory = mapOf(
            MissionCategory.ADVENTURE to listOf(
                "버스 대신 한 정거장 걸어가기" to Pair(MissionDifficulty.EASY, 15),
                "동네 숨은 맛집 찾기" to Pair(MissionDifficulty.MEDIUM, 25),
                "새로운 동네 탐방하기" to Pair(MissionDifficulty.MEDIUM, 30),
                "등산로 하나 완주하기" to Pair(MissionDifficulty.HARD, 50)
            ),
            MissionCategory.HEALTH to listOf(
                "계단으로 5층 올라가기" to Pair(MissionDifficulty.EASY, 10),
                "30분 산책하기" to Pair(MissionDifficulty.EASY, 20),
                "스트레칭 10분 하기" to Pair(MissionDifficulty.EASY, 15),
                "1시간 운동하기" to Pair(MissionDifficulty.MEDIUM, 40)
            ),
            MissionCategory.SOCIAL to listOf(
                "새로운 사람과 대화하기" to Pair(MissionDifficulty.MEDIUM, 25),
                "친구에게 안부 문자하기" to Pair(MissionDifficulty.EASY, 10),
                "동료와 점심 함께 먹기" to Pair(MissionDifficulty.EASY, 20),
                "새로운 모임 참가하기" to Pair(MissionDifficulty.HARD, 45)
            ),
            MissionCategory.CREATIVE to listOf(
                "그림 하나 그리기" to Pair(MissionDifficulty.MEDIUM, 30),
                "시 한 편 써보기" to Pair(MissionDifficulty.MEDIUM, 35),
                "사진 10장 찍기" to Pair(MissionDifficulty.EASY, 20),
                "새로운 요리 만들기" to Pair(MissionDifficulty.HARD, 50)
            ),
            MissionCategory.LEARNING to listOf(
                "새로운 단어 3개 외우기" to Pair(MissionDifficulty.EASY, 15),
                "온라인 강의 하나 듣기" to Pair(MissionDifficulty.MEDIUM, 40),
                "책 한 챕터 읽기" to Pair(MissionDifficulty.MEDIUM, 35),
                "새로운 기술 익히기" to Pair(MissionDifficulty.HARD, 60)
            )
        )
        
        val missions = similarMissionsByCategory[category] ?: similarMissionsByCategory[MissionCategory.ADVENTURE]!!
        return missions.take(3).mapIndexed { index, (title, difficultyAndPoints) ->
            SimilarMissionResponse(
                id = (1000 + index).toString(),
                title = title,
                difficulty = difficultyAndPoints.first.displayName,
                points = difficultyAndPoints.second
            )
        }
    }
}