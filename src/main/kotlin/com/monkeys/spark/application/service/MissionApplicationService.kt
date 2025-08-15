package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.MissionUseCase
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.domain.model.CategoryStat
import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.factory.MissionFactory
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.Rating
import com.monkeys.spark.domain.vo.mission.MissionStatus
import com.monkeys.spark.domain.vo.mission.MissionCategory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class MissionApplicationService(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository,
    private val missionFactory: MissionFactory,
    private val userApplicationService: UserApplicationService
) : MissionUseCase {

    override fun generateDailyMissions(userId: UserId): List<Mission> {
        // 사용자 존재 확인
        val user = userRepository.findById(userId) 
            ?: throw IllegalArgumentException("User not found: $userId")

        // 오늘 이미 생성된 미션이 있는지 확인
        val existingMissions = missionRepository.findTodaysMissionsByUserId(userId)
        if (existingMissions.isNotEmpty()) {
            return existingMissions
        }

        // 사용자 선호도 기반으로 3개의 미션 생성
        val missions = missionFactory.createDailyMissions(user)
        
        return missions.map { missionRepository.save(it) }
    }

    @Transactional
    override fun getTodaysMissions(userId: UserId): List<Mission> {
        // 사용자에게 할당된 미션들 중 ASSIGNED 상태인 것들을 조회
        val assignedMissions = missionRepository.findByUserIdAndStatus(userId, MissionStatus.ASSIGNED)
        
        // 할당된 미션이 없으면 자동으로 일일 미션 생성
        if (assignedMissions.isEmpty()) {
            return generateDailyMissions(userId)
        }
        
        return assignedMissions
    }

    @Transactional(readOnly = true)
    override fun getMissionDetail(missionId: MissionId): Mission? {
        return missionRepository.findById(missionId)
    }

    override fun startMission(command: StartMissionCommand): Mission {
        val missionId = MissionId(command.missionId)
        val userId = UserId(command.userId)
        
        val mission = missionRepository.findById(missionId) 
            ?: throw IllegalArgumentException("Mission not found: ${command.missionId}")

        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId != userId) {
            throw IllegalArgumentException("Mission does not belong to user: ${command.userId}")
        }

        mission.start()
        return missionRepository.save(mission)
    }

    override fun updateMissionProgress(command: UpdateProgressCommand): Mission {
        val missionId = MissionId(command.missionId)
        val userId = UserId(command.userId)
        
        val mission = missionRepository.findById(missionId) 
            ?: throw IllegalArgumentException("Mission not found: ${command.missionId}")

        // 미션 소유권 확인
        if (mission.userId != userId) {
            throw IllegalArgumentException("Mission does not belong to user: ${command.userId}")
        }

        mission.updateProgress(command.progress)
        return missionRepository.save(mission)
    }

    override fun completeMission(command: CompleteMissionCommand): Mission {
        val missionId = MissionId(command.missionId)
        val userId = UserId(command.userId)
        
        val mission = missionRepository.findById(missionId) 
            ?: throw IllegalArgumentException("Mission not found: ${command.missionId}")

        // 미션 소유권 확인
        if (mission.userId != userId) {
            throw IllegalArgumentException("Mission does not belong to user: ${command.userId}")
        }

        mission.complete()
        val completedMission = missionRepository.save(mission)

        // 사용자 포인트 및 통계 업데이트
        userApplicationService.addPoints(userId, mission.calculateFinalPoints())
        userApplicationService.incrementStreak(userId)
        userApplicationService.incrementCompletedMissions(userId)

        return completedMission
    }

    @Transactional(readOnly = true)
    override fun getOngoingMissions(userId: UserId): List<Mission> {
        return missionRepository.findByUserIdAndStatus(userId, MissionStatus.IN_PROGRESS)
    }

    @Transactional(readOnly = true)
    override fun getCompletedMissions(query: CompletedMissionsQuery): List<Mission> {
        val userId = UserId(query.userId)
        return missionRepository.findCompletedMissionsByUserId(userId, query.page, query.size)
    }

    @Transactional(readOnly = true)
    override fun getSimilarMissions(missionId: MissionId, limit: Int): List<Mission> {
        val mission = missionRepository.findById(missionId) 
            ?: throw IllegalArgumentException("Mission not found: $missionId")

        return missionRepository.findSimilarMissions(
            mission.category, 
            mission.difficulty, 
            missionId, 
            limit
        )
    }

    @Transactional(readOnly = true)
    override fun getPopularMissions(limit: Int): List<Mission> {
        return missionRepository.findPopularMissions(limit)
    }

    override fun rerollMissions(userId: UserId): List<Mission> {
        // 사용자 존재 확인
        userRepository.findById(userId) 
            ?: throw IllegalArgumentException("User not found: $userId")

        // 사용자에게 할당된 미션들 중 아직 시작하지 않은 미션들 반환
        return missionRepository.findByUserIdAndStatus(userId, MissionStatus.ASSIGNED)
    }

    @Transactional(readOnly = true)
    override fun getCategoryStatistics(userId: UserId): Map<MissionCategory, CategoryStat> {
        val categoryStats = missionRepository.getCategoryCompletionStats(userId)
        
        return MissionCategory.values().associateWith { category ->
            val completed = categoryStats[category] ?: 0
            val total = completed + 5 // 임시로 총 미션 수 계산 (실제로는 더 복잡한 로직 필요)
            
            CategoryStat(completed, total)
        }
    }

    override fun cleanupExpiredMissions(): Int {
        val expiredMissions = missionRepository.findExpiredMissions()
        var count = 0
        
        expiredMissions.forEach { mission ->
            if (mission.status != MissionStatus.EXPIRED) {
                mission.expire()
                missionRepository.save(mission)
                count++
            }
        }
        
        return count
    }

    /**
     * 미션 평점 추가
     */
    fun rateMission(missionId: MissionId, rating: Rating): Mission {
        val mission = missionRepository.findById(missionId) 
            ?: throw IllegalArgumentException("Mission not found: $missionId")

        mission.statistics.addRating(rating)
        return missionRepository.save(mission)
    }

    /**
     * 미션 완료 시간 기록
     */
    fun recordCompletionTime(missionId: MissionId, completionMinutes: Int): Mission {
        val mission = missionRepository.findById(missionId) 
            ?: throw IllegalArgumentException("Mission not found: $missionId")

        mission.statistics.updateCompletionTime(completionMinutes)
        return missionRepository.save(mission)
    }
}