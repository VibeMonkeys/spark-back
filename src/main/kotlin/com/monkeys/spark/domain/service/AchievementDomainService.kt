package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.achievement.AchievementType
import com.monkeys.spark.domain.vo.mission.MissionCategory

/**
 * 순수한 업적 도메인 서비스
 * 외부 의존성 없이 도메인 로직만 담당
 */
class AchievementDomainService {

    /**
     * 미션 완료 시 달성 가능한 업적들 확인
     */
    fun checkMissionAchievements(user: User, completedMission: Mission): List<AchievementType> {
        val achievements = mutableListOf<AchievementType>()

        // 첫 번째 미션 완료 업적
        if (user.completedMissions == 1) {
            achievements.add(AchievementType.FIRST_MISSION)
        }

        // 미션 개수 기반 업적들
        achievements.addAll(checkMissionCountAchievements(user.completedMissions))

        // 연속 달성 업적들
        achievements.addAll(checkStreakAchievements(user.currentStreak.value))

        // 카테고리별 전문가 업적들
        achievements.addAll(checkCategoryAchievements(completedMission.category))

        return achievements
    }

    /**
     * 포인트 획득 시 달성 가능한 업적들 확인
     */
    fun checkPointsAchievements(totalPoints: Int): List<AchievementType> {
        val achievements = mutableListOf<AchievementType>()

        if (totalPoints >= 1000) {
            achievements.add(AchievementType.POINTS_1000)
        }

        if (totalPoints >= 10000) {
            achievements.add(AchievementType.POINTS_10000)
        }

        return achievements
    }

    /**
     * 업적 진행도 계산
     */
    fun calculateProgress(user: User, achievementType: AchievementType): Int {
        return when (achievementType) {
            AchievementType.FIRST_MISSION -> if (user.completedMissions >= 1) 100 else 0
            AchievementType.MISSIONS_10 -> minOf(100, (user.completedMissions * 100) / 10)
            AchievementType.MISSIONS_50 -> minOf(100, (user.completedMissions * 100) / 50)
            AchievementType.MISSIONS_100 -> minOf(100, (user.completedMissions * 100) / 100)
            AchievementType.MISSION_STREAK_3 -> minOf(100, (user.currentStreak.value * 100) / 3)
            AchievementType.MISSION_STREAK_7 -> minOf(100, (user.currentStreak.value * 100) / 7)
            AchievementType.MISSION_STREAK_30 -> minOf(100, (user.currentStreak.value * 100) / 30)
            AchievementType.POINTS_1000 -> minOf(100, (user.totalPoints.value * 100) / 1000)
            AchievementType.POINTS_10000 -> minOf(100, (user.totalPoints.value * 100) / 10000)
            else -> 0
        }
    }

    private fun checkMissionCountAchievements(completedMissions: Int): List<AchievementType> {
        val achievements = mutableListOf<AchievementType>()

        when (completedMissions) {
            10 -> achievements.add(AchievementType.MISSIONS_10)
            50 -> achievements.add(AchievementType.MISSIONS_50)
            100 -> achievements.add(AchievementType.MISSIONS_100)
        }

        return achievements
    }

    private fun checkStreakAchievements(currentStreak: Int): List<AchievementType> {
        val achievements = mutableListOf<AchievementType>()

        when (currentStreak) {
            3 -> achievements.add(AchievementType.MISSION_STREAK_3)
            7 -> achievements.add(AchievementType.MISSION_STREAK_7)
            30 -> achievements.add(AchievementType.MISSION_STREAK_30)
        }

        return achievements
    }

    private fun checkCategoryAchievements(category: MissionCategory): List<AchievementType> {
        val achievements = mutableListOf<AchievementType>()

        // 실제로는 카테고리별 완료 횟수를 확인해야 하지만 
        // 여기서는 단순화하여 구현
        when (category) {
            MissionCategory.HEALTH -> {
                achievements.add(AchievementType.HEALTH_SPECIALIST)
            }

            MissionCategory.CREATIVE -> {
                achievements.add(AchievementType.CREATIVE_ARTIST)
            }

            MissionCategory.SOCIAL -> {
                achievements.add(AchievementType.SOCIAL_BUTTERFLY)
            }

            else -> { /* 다른 카테고리들 */
            }
        }

        return achievements
    }
}