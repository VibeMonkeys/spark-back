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
import com.monkeys.spark.domain.service.UserLevelDomainService
import com.monkeys.spark.domain.factory.MissionFactory
import com.monkeys.spark.domain.exception.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HomePageApplicationService(
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository,
    private val storyRepository: StoryRepository,
    private val userLevelDomainService: UserLevelDomainService,
    private val missionFactory: MissionFactory
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
            ?: throw UserNotFoundException(userId.value.toString())
        
        // 도메인 서비스를 통한 레벨 진행도 계산
        val progressToNextLevel = userLevelDomainService.calculateProgressToNextLevel(
            user.currentPoints.value, 
            user.level.value
        )
        val pointsToNextLevel = userLevelDomainService.calculatePointsToNextLevel(
            user.currentPoints.value, 
            user.level.value
        )
        
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
            return generateDailyMissionsUsingFactory(userId)
        }
        
        return existingMissions
    }
    
    @Transactional(readOnly = false)
    private fun generateDailyMissionsUsingFactory(userId: UserId): List<Mission> {
        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())
        
        // 템플릿 미션들 조회
        val templateMissions = missionRepository.findTemplateMissions()
        
        // 도메인 Factory를 통한 미션 생성
        val missions = missionFactory.createDailyMissions(user, templateMissions)
        
        // DB에 미션들 저장
        return missions.map { mission ->
            missionRepository.save(mission)
        }
    }
    
    @Transactional(readOnly = true)
    override fun getRecentStoriesForHome(limit: Int): List<Story> {
        return storyRepository.findRecentStories(limit)
    }
    
    // 계산 로직이 UserLevelDomainService로 이동됨
}