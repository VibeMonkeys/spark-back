package com.monkeys.spark.application.mapper

import com.monkeys.spark.application.dto.StoryFeedItem
import com.monkeys.spark.application.port.`in`.dto.UserSummary
import com.monkeys.spark.application.port.`in`.query.UserPointsSummary
import com.monkeys.spark.application.port.out.StoryRepository
import com.monkeys.spark.domain.model.*
import com.monkeys.spark.domain.service.LevelSystem
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.mission.MissionDifficulty
import com.monkeys.spark.domain.vo.mission.MissionStatus
import com.monkeys.spark.domain.vo.reward.RewardStatus
import com.monkeys.spark.domain.vo.stat.CategoryStat
import com.monkeys.spark.domain.vo.user.LevelInfo
import com.monkeys.spark.domain.vo.user.UserLevelTitle
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class ResponseMapper(
    private val storyRepository: StoryRepository
) {

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
            bio = user.bio,
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

        return response
    }

    /**
     * Story 도메인을 StoryResponse로 변환
     */
    fun toStoryResponse(story: Story, currentUserId: String?): StoryResponse {
        // 현재 사용자가 좋아요를 눌렀는지 확인
        val isLiked = currentUserId?.toLongOrNull()?.let {
            storyRepository.isLikedByUser(story.id, UserId(it))
        } ?: false

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
            id = userReward.id.value,
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
    fun toCategoryStatResponse(
        category: MissionCategory,
        stat: CategoryStat
    ): CategoryStatResponse {
        return CategoryStatResponse(
            name = category.displayName,
            completed = stat.completed,
            total = stat.total,
            percentage = stat.percentage.value,
            color = category.colorClass
        )
    }

    /**
     * 난이도와 카테고리에 따른 현실적인 미션 완료 인원 생성
     */
    private fun generateMockCompletedBy(
        difficulty: MissionDifficulty,
        category: MissionCategory
    ): Int {
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
    private fun generateMockAverageRating(
        difficulty: MissionDifficulty,
        category: MissionCategory
    ): Double {
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

        val rating =
            (baseRating.start + Math.random() * (baseRating.endInclusive - baseRating.start)) + categoryAdjustment
        return Math.round(rating * 10) / 10.0 // 소수점 첫째 자리까지
    }

    /**
     * 카테고리에 맞는 유사 미션 목록 생성
     */
    private fun generateMockSimilarMissions(category: MissionCategory): List<SimilarMissionResponse> {
        val similarMissionsByCategory = mapOf(
            MissionCategory.ADVENTURE to listOf(
                Triple(2L, "대중교통으로 끝까지 가기", Pair(MissionDifficulty.EASY, 15)),
                Triple(1L, "15분 산책하기", Pair(MissionDifficulty.EASY, 20)),
                Triple(3L, "카페에서 낯선 사람과 대화하기", Pair(MissionDifficulty.MEDIUM, 30))
            ),
            MissionCategory.HEALTH to listOf(
                Triple(4L, "계단 오르기 챌린지", Pair(MissionDifficulty.EASY, 10)),
                Triple(5L, "새로운 동네 탐험하기", Pair(MissionDifficulty.EASY, 15)),
                Triple(6L, "오늘의 하늘 그리기", Pair(MissionDifficulty.MEDIUM, 25))
            ),
            MissionCategory.SOCIAL to listOf(
                Triple(7L, "엘리베이터에서 인사하기", Pair(MissionDifficulty.EASY, 15)),
                Triple(5L, "새로운 동네 탐험하기", Pair(MissionDifficulty.EASY, 15)),
                Triple(8L, "하이쿠 한 편 쓰기", Pair(MissionDifficulty.MEDIUM, 30))
            ),
            MissionCategory.CREATIVE to listOf(
                Triple(8L, "하이쿠 한 편 쓰기", Pair(MissionDifficulty.MEDIUM, 30)),
                Triple(9L, "새로운 단어 5개 배우기", Pair(MissionDifficulty.EASY, 20)),
                Triple(1L, "15분 산책하기", Pair(MissionDifficulty.EASY, 20))
            ),
            MissionCategory.LEARNING to listOf(
                Triple(10L, "유튜브로 5분 강의 듣기", Pair(MissionDifficulty.EASY, 15)),
                Triple(6L, "오늘의 하늘 그리기", Pair(MissionDifficulty.MEDIUM, 25)),
                Triple(3L, "카페에서 낯선 사람과 대화하기", Pair(MissionDifficulty.MEDIUM, 30))
            )
        )

        val missions = similarMissionsByCategory[category] ?: similarMissionsByCategory[MissionCategory.ADVENTURE]!!
        return missions.take(3).map { (id, title, difficultyAndPoints) ->
            SimilarMissionResponse(
                id = id,
                title = title,
                difficulty = difficultyAndPoints.first.displayName,
                points = difficultyAndPoints.second
            )
        }
    }

    /**
     * 레벨 정보를 응답 DTO로 변환
     */
    fun toLevelInfoResponse(levelInfo: LevelInfo): LevelInfoResponse {
        return LevelInfoResponse(
            level = levelInfo.level,
            levelTitle = levelInfo.levelTitle.name,
            levelTitleDisplay = levelInfo.levelTitle.displayName,
            requiredPoints = levelInfo.requiredPoints,
            nextLevelPoints = levelInfo.nextLevelPoints,
            description = levelInfo.description,
            benefits = levelInfo.benefits,
            icon = levelInfo.icon,
            color = levelInfo.color,
            badge = levelInfo.badge
        )
    }

    /**
     * 사용자 레벨 진행 상황을 응답 DTO로 변환
     */
    fun toUserLevelProgressResponse(user: User): UserLevelProgressResponse {
        val currentLevelInfo = LevelSystem.getLevelInfo(user.level.value)
        val pointsToNextLevel = LevelSystem.getPointsToNextLevel(user.totalPoints.value)
        val progressPercentage = LevelSystem.getLevelProgress(user.totalPoints.value)
        val nextLevelPoints = if (user.level.value < 21) {
            LevelSystem.calculatePointsForLevel(user.level.value + 1)
        } else null

        return UserLevelProgressResponse(
            currentLevel = user.level.value,
            levelTitle = user.levelTitle.name,
            levelTitleDisplay = user.levelTitle.displayName,
            currentPoints = user.currentPoints.value,
            totalPoints = user.totalPoints.value,
            pointsToNextLevel = pointsToNextLevel,
            levelProgressPercentage = progressPercentage,
            nextLevelPoints = nextLevelPoints,
            icon = currentLevelInfo?.icon ?: "🌱",
            color = currentLevelInfo?.color ?: "#10B981",
            badge = currentLevelInfo?.badge ?: "beginner-badge"
        )
    }

    /**
     * 레벨 시스템 전체 정보를 응답 DTO로 변환
     */
    fun toLevelSystemResponse(user: User): LevelSystemResponse {
        val userProgress = toUserLevelProgressResponse(user)
        val allLevels = LevelSystem.getAllLevels().map { toLevelInfoResponse(it) }
        val levelTitles = createLevelTitleGroups()

        return LevelSystemResponse(
            userProgress = userProgress,
            allLevels = allLevels,
            levelTitles = levelTitles
        )
    }

    /**
     * 레벨 타이틀 그룹 생성
     */
    private fun createLevelTitleGroups(): List<LevelTitleGroupResponse> {
        val titleGroups = mapOf(
            UserLevelTitle.BEGINNER to "1-5",
            UserLevelTitle.EXPLORER to "6-10",
            UserLevelTitle.ADVENTURER to "11-20",
            UserLevelTitle.EXPERT to "21-30",
            UserLevelTitle.MASTER to "31-40",
            UserLevelTitle.GRANDMASTER to "41-45",
            UserLevelTitle.LEGEND to "46-50",
            UserLevelTitle.MYTHIC to "50+"
        )

        val descriptions = mapOf(
            UserLevelTitle.BEGINNER to "미션 여행을 시작하는 단계",
            UserLevelTitle.EXPLORER to "새로운 경험을 탐험하는 단계",
            UserLevelTitle.ADVENTURER to "진정한 모험을 시작하는 단계",
            UserLevelTitle.EXPERT to "전문가 수준의 경험을 쌓은 단계",
            UserLevelTitle.MASTER to "최고 수준의 마스터 단계",
            UserLevelTitle.GRANDMASTER to "초월적 경지의 그랜드마스터",
            UserLevelTitle.LEGEND to "전설적인 사용자 단계",
            UserLevelTitle.MYTHIC to "신화를 넘어선 절대적 존재"
        )

        val colors = mapOf(
            UserLevelTitle.BEGINNER to "#10B981",
            UserLevelTitle.EXPLORER to "#3B82F6",
            UserLevelTitle.ADVENTURER to "#F59E0B",
            UserLevelTitle.EXPERT to "#8B5CF6",
            UserLevelTitle.MASTER to "#DC2626",
            UserLevelTitle.GRANDMASTER to "#7C2D12",
            UserLevelTitle.LEGEND to "#7C3AED",
            UserLevelTitle.MYTHIC to "#1E1B4B"
        )

        val icons = mapOf(
            UserLevelTitle.BEGINNER to "🌱",
            UserLevelTitle.EXPLORER to "🔍",
            UserLevelTitle.ADVENTURER to "⚔️",
            UserLevelTitle.EXPERT to "🎓",
            UserLevelTitle.MASTER to "🏆",
            UserLevelTitle.GRANDMASTER to "👑",
            UserLevelTitle.LEGEND to "🚀",
            UserLevelTitle.MYTHIC to "✨"
        )

        return UserLevelTitle.values().map { titleEnum ->
            val levels = LevelSystem.getLevelsByTitle(titleEnum).map { toLevelInfoResponse(it) }

            LevelTitleGroupResponse(
                title = titleEnum.name,
                displayName = titleEnum.displayName,
                description = descriptions[titleEnum] ?: "",
                levelRange = titleGroups[titleEnum] ?: "",
                color = colors[titleEnum] ?: "#10B981",
                icon = icons[titleEnum] ?: "🌱",
                levels = levels
            )
        }.filter { it.levels.isNotEmpty() }
    }

    /**
     * Inquiry 도메인을 InquiryResponse로 변환
     */
    fun toInquiryResponse(inquiry: Inquiry): InquiryResponse {
        return InquiryResponse(
            id = inquiry.id.value.toString(),
            userId = inquiry.userId?.value?.toString(),
            email = inquiry.email,
            subject = inquiry.subject,
            message = inquiry.message,
            status = inquiry.status.name,
            statusDisplay = inquiry.status.displayName,
            response = inquiry.response,
            respondedAt = inquiry.respondedAt,
            respondedBy = inquiry.respondedBy,
            createdAt = inquiry.createdAt,
            updatedAt = inquiry.updatedAt
        )
    }

}