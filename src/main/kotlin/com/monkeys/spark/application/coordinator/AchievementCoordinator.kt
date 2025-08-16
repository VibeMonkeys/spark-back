package com.monkeys.spark.application.coordinator

import com.monkeys.spark.application.port.out.UserAchievementRepository
import com.monkeys.spark.application.port.out.UserStatsRepository
import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.achievement.AchievementType
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.stereotype.Component

/**
 * 업적 시스템 코디네이터
 * 여러 Application Service에서 공통으로 사용되는 업적 관련 비즈니스 로직을 관리
 * Repository 의존성이 필요한 복합적인 업적 처리를 담당
 */
@Component
class AchievementCoordinator(
    private val userAchievementRepository: UserAchievementRepository,
    private val userStatsRepository: UserStatsRepository
) {

    /**
     * 미션 완료 시 업적 확인 및 발급
     */
    fun checkAndGrantMissionAchievements(
        userId: UserId,
        missionCategory: String?
    ) {
        val userAchievements = userAchievementRepository.findByUserId(userId)
        val unlockedAchievementTypes = userAchievements.filter { it.isUnlocked() }.map { it.achievementType }
        val stats = userStatsRepository.findByUserId(userId) ?: return

        // 첫 번째 미션 완료 업적 - 실제로 1개 이상 미션을 완료했는지 확인
        if (stats.completedMissions >= 1 && AchievementType.FIRST_MISSION !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.FIRST_MISSION)
            userAchievementRepository.save(achievement)
        }

        // 미션 개수 기반 업적 확인
        checkMissionCountAchievements(userId, unlockedAchievementTypes)

        // 연속 달성 업적 확인
        checkStreakAchievements(userId, unlockedAchievementTypes)

        // 카테고리별 전문가 업적 확인
        missionCategory?.let {
            checkSpecialistAchievements(userId, it, unlockedAchievementTypes)
        }
    }

    /**
     * 포인트 획득 시 업적 확인
     */
    fun checkAndGrantPointsAchievements(
        userId: UserId,
        totalPoints: Int
    ) {
        val userAchievements = userAchievementRepository.findByUserId(userId)
        val unlockedAchievementTypes = userAchievements.filter { it.isUnlocked() }.map { it.achievementType }

        // 1,000 포인트 업적
        if (totalPoints >= 1000 && AchievementType.POINTS_1000 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.POINTS_1000)
            userAchievementRepository.save(achievement)
        }

        // 10,000 포인트 업적
        if (totalPoints >= 10000 && AchievementType.POINTS_10000 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.POINTS_10000)
            userAchievementRepository.save(achievement)
        }
    }

    /**
     * 사용자의 모든 업적 조회 (달성된 것과 진행 중인 것 포함)
     * 100% 달성한 업적은 자동으로 잠금 해제함
     */
    fun getUserAchievements(userId: UserId): List<UserAchievement> {
        val userAchievements = userAchievementRepository.findByUserId(userId)
        val achievedTypes = userAchievements.map { it.achievementType }.toSet()

        // 아직 달성하지 않은 업적들을 확인하고 진행도 계산
        val allAchievements = mutableListOf<UserAchievement>()
        allAchievements.addAll(userAchievements)

        AchievementType.values().forEach { achievementType ->
            if (achievementType !in achievedTypes) {
                // 진행 중인 업적인지 확인하고 진행도 계산
                val progress = calculateAchievementProgress(userId, achievementType)

                if (progress >= 100) {
                    // 100% 달성한 업적은 자동으로 잠금 해제
                    val achievement = UserAchievement.unlock(userId, achievementType)
                    userAchievementRepository.save(achievement)
                    allAchievements.add(achievement)
                } else if (progress > 0) {
                    allAchievements.add(
                        UserAchievement.inProgress(userId, achievementType, progress)
                    )
                } else {
                    // 0% 진행도 업적도 UI에서 보여주기 위해 추가
                    allAchievements.add(
                        UserAchievement.inProgress(userId, achievementType, 0)
                    )
                }
            }
        }

        return allAchievements.sortedBy { it.achievementType.rarity.order }
    }

    /**
     * 미션 개수 기반 업적 확인
     */
    private fun checkMissionCountAchievements(
        userId: UserId,
        unlockedAchievementTypes: List<AchievementType>
    ) {
        val stats = userStatsRepository.findByUserId(userId) ?: return
        val completedMissions = stats.completedMissions

        // 10개 미션 완료
        if (completedMissions >= 10 && AchievementType.MISSIONS_10 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.MISSIONS_10)
            userAchievementRepository.save(achievement)
        }

        // 50개 미션 완료
        if (completedMissions >= 50 && AchievementType.MISSIONS_50 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.MISSIONS_50)
            userAchievementRepository.save(achievement)
        }

        // 100개 미션 완료
        if (completedMissions >= 100 && AchievementType.MISSIONS_100 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.MISSIONS_100)
            userAchievementRepository.save(achievement)
        }
    }

    /**
     * 연속 달성 업적 확인
     */
    private fun checkStreakAchievements(
        userId: UserId,
        unlockedAchievementTypes: List<AchievementType>
    ) {
        val stats = userStatsRepository.findByUserId(userId) ?: return
        val currentStreak = stats.currentStreak

        // 3일 연속
        if (currentStreak >= 3 && AchievementType.MISSION_STREAK_3 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.MISSION_STREAK_3)
            userAchievementRepository.save(achievement)
        }

        // 7일 연속
        if (currentStreak >= 7 && AchievementType.MISSION_STREAK_7 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.MISSION_STREAK_7)
            userAchievementRepository.save(achievement)
        }

        // 30일 연속
        if (currentStreak >= 30 && AchievementType.MISSION_STREAK_30 !in unlockedAchievementTypes) {
            val achievement = UserAchievement.unlock(userId, AchievementType.MISSION_STREAK_30)
            userAchievementRepository.save(achievement)
        }
    }

    /**
     * 카테고리별 전문가 업적 확인
     */
    private fun checkSpecialistAchievements(
        userId: UserId,
        missionCategory: String,
        unlockedAchievementTypes: List<AchievementType>
    ) {
        // 실제로는 미션 히스토리에서 카테고리별 완료 횟수를 조회해야 하지만
        // 여기서는 간단히 구현 (실제 구현 시 MissionHistoryPort 등을 추가로 만들어야 함)

        when (missionCategory.uppercase()) {
            "HEALTH" -> {
                if (AchievementType.HEALTH_SPECIALIST !in unlockedAchievementTypes) {
                    // 카테고리별 완료 횟수 체크 로직 (임시로 10회 달성으로 가정)
                    val achievement = UserAchievement.unlock(userId, AchievementType.HEALTH_SPECIALIST)
                    userAchievementRepository.save(achievement)
                }
            }

            "CREATIVE" -> {
                if (AchievementType.CREATIVE_ARTIST !in unlockedAchievementTypes) {
                    val achievement = UserAchievement.unlock(userId, AchievementType.CREATIVE_ARTIST)
                    userAchievementRepository.save(achievement)
                }
            }

            "SOCIAL" -> {
                if (AchievementType.SOCIAL_BUTTERFLY !in unlockedAchievementTypes) {
                    val achievement = UserAchievement.unlock(userId, AchievementType.SOCIAL_BUTTERFLY)
                    userAchievementRepository.save(achievement)
                }
            }
        }
    }

    /**
     * 특정 업적의 진행도 계산
     */
    private fun calculateAchievementProgress(
        userId: UserId,
        achievementType: AchievementType
    ): Int {
        val stats = userStatsRepository.findByUserId(userId) ?: return 0

        return when (achievementType) {
            AchievementType.FIRST_MISSION -> if (stats.completedMissions >= 1) 100 else 0
            AchievementType.MISSIONS_10 -> minOf(100, (stats.completedMissions * 100) / 10)
            AchievementType.MISSIONS_50 -> minOf(100, (stats.completedMissions * 100) / 50)
            AchievementType.MISSIONS_100 -> minOf(100, (stats.completedMissions * 100) / 100)
            AchievementType.MISSION_STREAK_3 -> minOf(100, (stats.currentStreak * 100) / 3)
            AchievementType.MISSION_STREAK_7 -> minOf(100, (stats.currentStreak * 100) / 7)
            AchievementType.MISSION_STREAK_30 -> minOf(100, (stats.currentStreak * 100) / 30)
            AchievementType.POINTS_1000 -> minOf(100, (stats.totalPoints * 100) / 1000)
            AchievementType.POINTS_10000 -> minOf(100, (stats.totalPoints * 100) / 10000)
            else -> 0
        }
    }

}