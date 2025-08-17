package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.MissionUseCase
import com.monkeys.spark.application.port.`in`.NotificationUseCase
import com.monkeys.spark.application.port.`in`.command.AbandonMissionCommand
import com.monkeys.spark.application.port.`in`.command.CompleteMissionCommand
import com.monkeys.spark.application.port.`in`.command.StartMissionCommand
import com.monkeys.spark.application.port.`in`.command.UpdateProgressCommand
import com.monkeys.spark.application.port.`in`.query.CompletedMissionsQuery
import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.exception.BusinessRuleViolationException
import com.monkeys.spark.domain.exception.MissionNotFoundException
import com.monkeys.spark.domain.exception.UserNotFoundException
import com.monkeys.spark.domain.factory.MissionFactory
import com.monkeys.spark.domain.vo.stat.CategoryStat
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.service.UserMissionDomainService
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.Rating
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.mission.MissionStatus
import com.monkeys.spark.domain.model.Notification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MissionApplicationService(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository,
    private val userMissionDomainService: UserMissionDomainService,
    private val missionFactory: MissionFactory,
    private val notificationUseCase: NotificationUseCase
) : MissionUseCase {

    override fun generateDailyMissions(userId: UserId): List<Mission> {
        // 사용자 존재 확인
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        // 오늘 이미 생성된 미션이 있는지 확인
        val existingMissions = missionRepository.findTodaysMissionsByUserId(userId)
        if (existingMissions.isNotEmpty()) {
            return existingMissions
        }

        // 템플릿 미션들 조회
        val templateMissions = missionRepository.findTemplateMissions()

        // 도메인 Factory를 통한 사용자 선호도 기반 미션 생성
        val missions = missionFactory.createDailyMissions(user, templateMissions)

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
    override fun getMissionDetail(missionId: MissionId): Mission {
        return missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(missionId.value.toString())
    }

    override fun startMission(command: StartMissionCommand): Mission {
        val missionId = MissionId(command.missionId)
        val userId = UserId(command.userId)

        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(command.missionId.toString())

        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId != userId) {
            throw BusinessRuleViolationException("Mission does not belong to user: ${command.userId}")
        }

        mission.start()
        val savedMission = missionRepository.save(mission)
        
        // 미션 시작 알림 전송
        val notification = Notification.missionStarted(userId, mission.title.value)
        notificationUseCase.sendNotification(notification)
        
        return savedMission
    }

    override fun updateMissionProgress(command: UpdateProgressCommand): Mission {
        val missionId = MissionId(command.missionId)
        val userId = UserId(command.userId)

        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(command.missionId.toString())

        // 미션 소유권 확인
        if (mission.userId != userId) {
            throw BusinessRuleViolationException("Mission does not belong to user: ${command.userId}")
        }

        mission.updateProgress(command.progress)
        return missionRepository.save(mission)
    }

    override fun completeMission(command: CompleteMissionCommand): Mission {
        val missionId = MissionId(command.missionId)
        val userId = UserId(command.userId)

        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(command.missionId.toString())

        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        // 도메인 서비스를 통한 비즈니스 규칙 검증
        if (!userMissionDomainService.canCompleteMission(user, mission)) {
            throw BusinessRuleViolationException("Mission cannot be completed by user: ${command.userId}")
        }

        // 보너스 포인트 계산
        val bonusPoints = userMissionDomainService.calculateBonusPoints(user, mission)

        // 도메인 모델에서 미션 완료 처리
        mission.complete()
        user.completeMission(mission)

        // 보너스 포인트 추가
        if (bonusPoints > 0) {
            user.earnPoints(com.monkeys.spark.domain.vo.common.Points(bonusPoints))
        }

        // 저장
        missionRepository.save(mission)
        userRepository.save(user)

        // 미션 완료 알림 전송
        val totalPoints = mission.rewardPoints.value + bonusPoints
        val notification = Notification.missionCompleted(userId, mission.title.value, totalPoints)
        notificationUseCase.sendNotification(notification)

        return mission
    }

    override fun abandonMission(command: AbandonMissionCommand): Mission {
        val userId = UserId(command.userId)
        val missionId = MissionId(command.missionId)

        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(command.missionId.toString())

        // 미션 소유권 확인
        if (mission.userId != userId) {
            throw BusinessRuleViolationException("Mission does not belong to user: ${command.userId}")
        }

        // 진행 중인 미션만 포기 가능
        if (mission.status != MissionStatus.IN_PROGRESS) {
            throw BusinessRuleViolationException("Only in-progress missions can be abandoned")
        }

        mission.expire()
        return missionRepository.save(mission)
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
            ?: throw MissionNotFoundException(missionId.value.toString())

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

    @Transactional
    override fun rerollMissions(userId: UserId): List<Mission> {
        // 사용자 존재 확인
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.value.toString())

        // 기존 ASSIGNED, IN_PROGRESS 상태의 미션들 삭제
        val assignedMissions = missionRepository.findByUserIdAndStatus(userId, MissionStatus.ASSIGNED)
        val inProgressMissions = missionRepository.findByUserIdAndStatus(userId, MissionStatus.IN_PROGRESS)

        (assignedMissions + inProgressMissions).forEach { mission ->
            missionRepository.deleteById(mission.id)
        }

        // 팩토리를 직접 호출해서 새로운 미션 생성 (기존 미션 체크 건너뛰기)
        val templateMissions = missionRepository.findTemplateMissions()
        val missions = missionFactory.createDailyMissions(user, templateMissions)
        return missions.map { missionRepository.save(it) }
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
            ?: throw MissionNotFoundException(missionId.value.toString())

        mission.statistics.addRating(rating)
        return missionRepository.save(mission)
    }

    /**
     * 미션 완료 시간 기록
     */
    fun recordCompletionTime(missionId: MissionId, completionMinutes: Int): Mission {
        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(missionId.value.toString())

        mission.statistics.updateCompletionTime(completionMinutes)
        return missionRepository.save(mission)
    }
}