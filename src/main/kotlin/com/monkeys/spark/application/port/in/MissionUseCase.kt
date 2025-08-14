package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.CategoryStat
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*

/**
 * 미션 관련 UseCase 인터페이스
 */
interface MissionUseCase {
    
    /**
     * 일일 미션 생성 (사용자별로 하루에 3개)
     */
    fun generateDailyMissions(userId: UserId): List<Mission>
    
    /**
     * 오늘의 미션 조회
     */
    fun getTodaysMissions(userId: UserId): List<Mission>
    
    /**
     * 미션 상세 조회
     */
    fun getMissionDetail(missionId: MissionId): Mission?
    
    /**
     * 미션 시작
     */
    fun startMission(command: StartMissionCommand): Mission
    
    /**
     * 미션 진행도 업데이트
     */
    fun updateMissionProgress(command: UpdateProgressCommand): Mission
    
    /**
     * 미션 완료
     */
    fun completeMission(command: CompleteMissionCommand): Mission
    
    /**
     * 진행 중인 미션 조회
     */
    fun getOngoingMissions(userId: UserId): List<Mission>
    
    /**
     * 완료된 미션 조회 (페이징)
     */
    fun getCompletedMissions(query: CompletedMissionsQuery): List<Mission>
    
    /**
     * 유사 미션 조회
     */
    fun getSimilarMissions(missionId: MissionId, limit: Int): List<Mission>
    
    /**
     * 인기 미션 조회
     */
    fun getPopularMissions(limit: Int): List<Mission>
    
    /**
     * 미션 리롤 (새로운 미션으로 교체)
     */
    fun rerollMissions(userId: UserId): List<Mission>
    
    /**
     * 카테고리별 미션 통계
     */
    fun getCategoryStatistics(userId: UserId): Map<MissionCategory, CategoryStat>
    
    /**
     * 만료된 미션 정리
     */
    fun cleanupExpiredMissions(): Int
}

