package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.vo.common.Location
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.mission.MissionDifficulty
import com.monkeys.spark.domain.vo.mission.MissionStatus
import com.monkeys.spark.domain.vo.mission.StartMissionValidation
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.MissionPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.MissionJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MissionPersistenceAdapter(
    private val missionJpaRepository: MissionJpaRepository,
    private val missionMapper: MissionPersistenceMapper
) : MissionRepository {

    override fun save(mission: Mission): Mission {
        val entity = missionMapper.toEntity(mission)
        val savedEntity = missionJpaRepository.save(entity)
        return missionMapper.toDomain(savedEntity)
    }

    override fun findById(id: MissionId): Mission? {
        return missionJpaRepository.findById(id.value)
            .map { missionMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: UserId): List<Mission> {
        return missionJpaRepository.findByUserId(userId.value)
            .map { missionMapper.toDomain(it) }
    }

    override fun findByUserIdAndStatus(
        userId: UserId,
        status: MissionStatus
    ): List<Mission> {
        return missionJpaRepository.findByUserIdAndStatus(userId.value, status.name)
            .map { missionMapper.toDomain(it) }
    }

    override fun findTodaysMissionsByUserId(userId: UserId): List<Mission> {
        val today = LocalDateTime.now().toLocalDate()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.plusDays(1).atStartOfDay()

        return missionJpaRepository.findByUserIdAndCreatedAtBetween(
            userId.value,
            startOfDay,
            endOfDay
        ).map { missionMapper.toDomain(it) }
    }

    override fun findOngoingMissionsByUserId(userId: UserId): List<Mission> {
        return missionJpaRepository.findByUserIdAndStatus(userId.value, MissionStatus.IN_PROGRESS.name)
            .map { missionMapper.toDomain(it) }
    }

    override fun findCompletedMissionsByUserId(
        userId: UserId,
        page: Int, size: Int
    ): List<Mission> {
        // 임시 구현 - 실제로는 Pageable을 사용해야 함
        return missionJpaRepository.findByUserIdAndStatus(userId.value, MissionStatus.COMPLETED.name)
            .drop(page * size)
            .take(size)
            .map { missionMapper.toDomain(it) }
    }

    override fun findByCategory(category: MissionCategory): List<Mission> {
        return missionJpaRepository.findByCategory(category.name)
            .map { missionMapper.toDomain(it) }
    }

    override fun findByDifficulty(difficulty: MissionDifficulty): List<Mission> {
        return missionJpaRepository.findByDifficulty(difficulty.name)
            .map { missionMapper.toDomain(it) }
    }

    override fun findExpiredMissions(): List<Mission> {
        return missionJpaRepository.findExpiredMissions(LocalDateTime.now())
            .map { missionMapper.toDomain(it) }
    }

    override fun findSimilarMissions(
        category: MissionCategory,
        difficulty: MissionDifficulty,
        excludeId: MissionId,
        limit: Int
    ): List<Mission> {
        return missionJpaRepository.findByCategoryAndDifficultyAndIdNot(
            category.name,
            difficulty.name,
            excludeId.value
        ).take(limit).map { missionMapper.toDomain(it) }
    }

    override fun findPopularMissions(limit: Int): List<Mission> {
        return missionJpaRepository.findAll()
            .sortedByDescending { it.completedCount ?: 0 }
            .take(limit)
            .map { missionMapper.toDomain(it) }
    }

    override fun findMissionTemplates(): List<Mission> {
        // 임시 구현 - 실제로는 템플릿 미션들을 따로 관리해야 함
        return emptyList()
    }

    override fun findTemplateMissions(): List<Mission> {
        return missionJpaRepository.findByIsTemplate(true)
            .map { missionMapper.toDomain(it) }
    }

    override fun findRecommendedMissions(
        userId: UserId,
        preferences: Map<MissionCategory, Boolean>,
        limit: Int
    ): List<Mission> {
        val preferredCategories = preferences.filter { it.value }.keys.map { it.name }
        return if (preferredCategories.isNotEmpty()) {
            missionJpaRepository.findByCategoryInAndStatus(
                preferredCategories,
                MissionStatus.ASSIGNED.name
            ).take(limit).map { missionMapper.toDomain(it) }
        } else {
            missionJpaRepository.findByStatus(MissionStatus.ASSIGNED.name)
                .take(limit)
                .map { missionMapper.toDomain(it) }
        }
    }

    override fun findMissionsByConditions(
        timeOfDay: String?,
        weatherCondition: String?,
        location: Location?
    ): List<Mission> {
        var missions = missionJpaRepository.findAll()

        timeOfDay?.let { time ->
            missions = missions.filter { mission ->
                mission.availableTimeSlots?.contains(time) == true
            }
        }

        weatherCondition?.let { weather ->
            missions = missions.filter { mission ->
                mission.weatherConditions?.contains(weather) == true
            }
        }

        location?.let { loc ->
            missions = missions.filter { mission ->
                mission.location?.contains(loc.value) == true
            }
        }

        return missions.map { missionMapper.toDomain(it) }
    }

    override fun updateStatistics(
        missionId: MissionId,
        completedBy: Int,
        averageRating: Double
    ): Mission? {
        return findById(missionId)?.let { mission ->
            // 통계 업데이트 로직 - 실제로는 별도 테이블로 관리
            save(mission)
        }
    }

    override fun countByCreatedAtBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Long {
        return missionJpaRepository.countByCreatedAtBetween(startDate, endDate)
    }

    override fun calculateCompletionRateByUserId(userId: UserId): Double {
        val totalMissions = missionJpaRepository.countByUserId(userId.value)
        val completedMissions = missionJpaRepository.countByUserIdAndStatus(
            userId.value,
            MissionStatus.COMPLETED.name
        )

        return if (totalMissions > 0) {
            (completedMissions.toDouble() / totalMissions) * 100
        } else {
            0.0
        }
    }

    override fun getCategoryCompletionStats(userId: UserId): Map<MissionCategory, Int> {
        return MissionCategory.values().associateWith { category ->
            missionJpaRepository.countByUserIdAndCategoryAndStatus(
                userId.value,
                category.name,
                MissionStatus.COMPLETED.name
            ).toInt()
        }
    }

    override fun deleteById(missionId: MissionId) {
        missionJpaRepository.deleteById(missionId.value)
    }

    override fun deleteByUserId(userId: UserId) {
        missionJpaRepository.deleteByUserId(userId.value)
    }

    override fun findAvailableTemplatesForUser(userId: UserId): List<Mission> {
        return missionJpaRepository.findAvailableTemplatesForUser(userId.value)
            .map { missionMapper.toDomain(it) }
    }

    override fun findRandomAvailableTemplatesForUser(
        userId: UserId,
        limit: Int
    ): List<Mission> {
        return missionJpaRepository.findRandomAvailableTemplatesForUser(userId.value, limit)
            .map { missionMapper.toDomain(it) }
    }

    override fun findUserCompletedOrOngoingTemplateIds(userId: UserId): List<String> {
        return missionJpaRepository.findUserCompletedOrOngoingTemplateIds(userId.value)
    }

    override fun countTodayStartedMissions(userId: UserId): Long {
        val today = LocalDateTime.now().toLocalDate()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.plusDays(1).atStartOfDay()

        return missionJpaRepository.countByUserIdAndStartedAtBetween(
            userId.value,
            startOfDay,
            endOfDay
        )
    }

    override fun canStartMission(userId: UserId): StartMissionValidation {
        // 일일 미션 시작 제한만 체크 (진행 중인 미션 체크 제거 - 여러 미션 동시 진행 허용)
        val today = LocalDateTime.now().toLocalDate()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.plusDays(1).atStartOfDay()

        // 오늘 시작한 미션 수 체크 (이미 최적화된 메서드 사용)
        val todayStartedCount = missionJpaRepository.countByUserIdAndStartedAtBetween(
            userId.value,
            startOfDay,
            endOfDay
        )
        if (todayStartedCount >= 3) {
            return StartMissionValidation.dailyLimitExceeded(todayStartedCount)
        }

        // 진행 중인 미션 수 정보 제공 (제한은 하지 않음)
        val ongoingCount = missionJpaRepository.countByUserIdAndStatus(userId.value, MissionStatus.IN_PROGRESS.name)

        return StartMissionValidation.allowedToStart(todayStartedCount)
    }

}