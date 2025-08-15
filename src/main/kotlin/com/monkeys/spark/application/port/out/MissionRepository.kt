package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.StartMissionValidation
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.Location
import com.monkeys.spark.domain.vo.mission.MissionStatus
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.mission.MissionDifficulty
import java.time.LocalDateTime

interface MissionRepository {
    
    /**
     * 미션 저장 (생성 및 수정)
     */
    fun save(mission: Mission): Mission
    
    /**
     * 미션 ID로 조회
     */
    fun findById(missionId: MissionId): Mission?
    
    /**
     * 사용자의 모든 미션 조회
     */
    fun findByUserId(userId: UserId): List<Mission>
    
    /**
     * 사용자의 특정 상태 미션들 조회
     */
    fun findByUserIdAndStatus(userId: UserId, status: MissionStatus): List<Mission>
    
    /**
     * 사용자의 오늘 배정된 미션들 조회
     */
    fun findTodaysMissionsByUserId(userId: UserId): List<Mission>
    
    /**
     * 사용자의 진행 중인 미션들 조회
     */
    fun findOngoingMissionsByUserId(userId: UserId): List<Mission>
    
    /**
     * 사용자의 완료된 미션들 조회 (페이징)
     */
    fun findCompletedMissionsByUserId(userId: UserId, page: Int, size: Int): List<Mission>
    
    /**
     * 카테고리별 미션 조회
     */
    fun findByCategory(category: MissionCategory): List<Mission>
    
    /**
     * 난이도별 미션 조회
     */
    fun findByDifficulty(difficulty: MissionDifficulty): List<Mission>
    
    /**
     * 만료된 미션들 조회
     */
    fun findExpiredMissions(): List<Mission>
    
    /**
     * 특정 미션과 유사한 미션들 조회
     */
    fun findSimilarMissions(category: MissionCategory, difficulty: MissionDifficulty, excludeId: MissionId, limit: Int): List<Mission>
    
    /**
     * 인기 미션들 조회 (완료수 기준)
     */
    fun findPopularMissions(limit: Int): List<Mission>
    
    /**
     * 미션 템플릿 조회 (시스템에서 제공하는 기본 미션들)
     */
    fun findMissionTemplates(): List<Mission>
    
    /**
     * 템플릿 미션들 조회 (is_template = true)
     */
    fun findTemplateMissions(): List<Mission>
    
    /**
     * 사용자가 아직 시도하지 않은 템플릿 미션들 조회
     */
    fun findAvailableTemplatesForUser(userId: UserId): List<Mission>
    
    /**
     * 사용자가 아직 시도하지 않은 템플릿 미션들 중 랜덤으로 조회
     */
    fun findRandomAvailableTemplatesForUser(userId: UserId, limit: Int): List<Mission>
    
    /**
     * 사용자가 완료했거나 현재 진행 중인 템플릿 미션 ID들 조회
     */
    fun findUserCompletedOrOngoingTemplateIds(userId: UserId): List<String>
    
    /**
     * 사용자 선호도 기반 미션 추천
     */
    fun findRecommendedMissions(userId: UserId, preferences: Map<MissionCategory, Boolean>, limit: Int): List<Mission>
    
    /**
     * 날씨/시간 조건에 맞는 미션 조회
     */
    fun findMissionsByConditions(
        timeOfDay: String? = null,
        weatherCondition: String? = null,
        location: Location? = null
    ): List<Mission>
    
    /**
     * 미션 통계 업데이트
     */
    fun updateStatistics(missionId: MissionId, completedBy: Int, averageRating: Double): Mission?
    
    /**
     * 특정 기간 동안 생성된 미션 수
     */
    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long
    
    /**
     * 사용자별 미션 완료율 조회
     */
    fun calculateCompletionRateByUserId(userId: UserId): Double
    
    /**
     * 카테고리별 미션 완료 통계 조회
     */
    fun getCategoryCompletionStats(userId: UserId): Map<MissionCategory, Int>
    
    /**
     * 미션 삭제
     */
    fun deleteById(missionId: MissionId)
    
    /**
     * 사용자의 미션 전체 삭제
     */
    fun deleteByUserId(userId: UserId)
    
    /**
     * 오늘 시작한 미션 개수 조회 (일일 제한 체크용)
     */
    fun countTodayStartedMissions(userId: UserId): Long
    
    /**
     * 미션 시작 가능 여부를 한 번에 체크 (진행 중인 미션 존재 여부 + 오늘 시작한 미션 수)
     */
    fun canStartMission(userId: UserId): StartMissionValidation
}