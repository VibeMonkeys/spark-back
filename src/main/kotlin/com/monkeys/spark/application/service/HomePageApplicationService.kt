package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.HomePageUseCase
import com.monkeys.spark.application.port.`in`.dto.HomePageData
import com.monkeys.spark.application.port.`in`.dto.UserSummary
import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.application.port.out.StoryRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HomePageApplicationService(
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository,
    private val storyRepository: StoryRepository
) : HomePageUseCase {

    override fun getHomePageData(userId: UserId): HomePageData {
        val userSummary = getUserSummary(userId)
        val todaysMissions = getTodaysRecommendedMissions(userId)
        val recentStories = getRecentStoriesForHome(5)
        
        return HomePageData(
            userSummary = userSummary,
            todaysMissions = todaysMissions,
            recentStories = recentStories
        )
    }
    
    @Transactional(readOnly = true)
    override fun getUserSummary(userId: UserId): UserSummary {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")
        
        // TODO: 실제 레벨 진행도 계산 로직 구현
        val progressToNextLevel = calculateProgressToNextLevel(user.currentPoints.value, user.level.value)
        val pointsToNextLevel = calculatePointsToNextLevel(user.currentPoints.value, user.level.value)
        
        return UserSummary(
            user = user,
            progressToNextLevel = progressToNextLevel,
            pointsToNextLevel = pointsToNextLevel
        )
    }
    
    override fun getTodaysRecommendedMissions(userId: UserId): List<Mission> {
        val existingMissions = missionRepository.findTodaysMissionsByUserId(userId)
        
        // 기존 미션이 없으면 사용자에게 새로운 미션들을 할당
        if (existingMissions.isEmpty()) {
            return createAndAssignDailyMissions(userId)
        }
        
        return existingMissions
    }
    
    @Transactional(readOnly = false)
    private fun createAndAssignDailyMissions(userId: UserId): List<Mission> {
        // 실제 DB에 저장할 미션들 생성
        val missions = listOf(
            Mission.createSample(
                id = com.monkeys.spark.domain.vo.common.MissionId.generate(),
                userId = userId,
                title = "30분 산책하기",
                description = "신선한 공기를 마시며 동네를 산책해보세요",
                category = com.monkeys.spark.domain.vo.mission.MissionCategory.HEALTH,
                difficulty = com.monkeys.spark.domain.vo.mission.MissionDifficulty.EASY,
                rewardPoints = 20
            ),
            Mission.createSample(
                id = com.monkeys.spark.domain.vo.common.MissionId.generate(),
                userId = userId,
                title = "새로운 요리 만들기",
                description = "평소 만들어보지 않은 요리에 도전해보세요",
                category = com.monkeys.spark.domain.vo.mission.MissionCategory.CREATIVE,
                difficulty = com.monkeys.spark.domain.vo.mission.MissionDifficulty.MEDIUM,
                rewardPoints = 30
            ),
            Mission.createSample(
                id = com.monkeys.spark.domain.vo.common.MissionId.generate(),
                userId = userId,
                title = "친구에게 안부 인사하기",
                description = "오랫동안 연락하지 않은 친구에게 메시지를 보내보세요",
                category = com.monkeys.spark.domain.vo.mission.MissionCategory.SOCIAL,
                difficulty = com.monkeys.spark.domain.vo.mission.MissionDifficulty.EASY,
                rewardPoints = 15
            )
        )
        
        // DB에 미션들 저장
        return missions.map { mission ->
            missionRepository.save(mission)
        }
    }
    
    @Transactional(readOnly = true)
    override fun getRecentStoriesForHome(limit: Int): List<Story> {
        return storyRepository.findRecentStories(limit)
    }
    
    private fun calculateProgressToNextLevel(currentPoints: Int, currentLevel: Int): Int {
        // TODO: 실제 레벨 시스템에 따른 계산 로직 구현
        val pointsForCurrentLevel = currentLevel * 1000 // 임시 계산
        val pointsForNextLevel = (currentLevel + 1) * 1000
        val progressPoints = currentPoints - pointsForCurrentLevel
        val requiredPoints = pointsForNextLevel - pointsForCurrentLevel
        
        return if (requiredPoints > 0) {
            ((progressPoints.toDouble() / requiredPoints) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }
    
    private fun calculatePointsToNextLevel(currentPoints: Int, currentLevel: Int): Int {
        // TODO: 실제 레벨 시스템에 따른 계산 로직 구현
        val pointsForNextLevel = (currentLevel + 1) * 1000
        return (pointsForNextLevel - currentPoints).coerceAtLeast(0)
    }
}