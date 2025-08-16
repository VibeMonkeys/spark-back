package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.mission.MissionStatus

/**
 * 스토리 생성 시 미션 완료 처리를 담당하는 순수 도메인 서비스
 */
class StoryMissionDomainService {
    
    /**
     * 스토리 생성 시 미션을 완료할 수 있는지 검증
     */
    fun canCompleteMissionFromStory(user: User, mission: Mission): Boolean {
        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId != user.id) {
            return false
        }
        
        // 이미 완료된 미션은 다시 완료할 수 없음
        if (mission.status == MissionStatus.COMPLETED) {
            return false
        }
        
        // ASSIGNED 또는 IN_PROGRESS 상태의 미션만 완료 가능
        return mission.status in listOf(MissionStatus.ASSIGNED, MissionStatus.IN_PROGRESS)
    }
    
    /**
     * 스토리 생성을 통한 미션 완료 처리
     * 도메인 규칙: 스토리를 작성하면 자동으로 미션이 완료됨
     */
    fun completeMissionFromStory(user: User, mission: Mission): Pair<User, Mission> {
        require(canCompleteMissionFromStory(user, mission)) {
            "Mission cannot be completed from story creation"
        }
        
        // 미션이 ASSIGNED 상태인 경우 시작 처리
        if (mission.status == MissionStatus.ASSIGNED) {
            mission.start()
        }
        
        // 미션 완료 처리
        val completedMission = mission.complete()
        
        // 사용자에게 미션 완료 반영 (포인트, 통계 등)
        val updatedUser = user.completeMission(completedMission)
        
        return Pair(updatedUser, completedMission)
    }
}