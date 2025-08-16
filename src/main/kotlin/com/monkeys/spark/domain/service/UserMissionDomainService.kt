package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.User

/**
 * 사용자-미션 관련 도메인 서비스
 * 복잡한 도메인 로직이나 여러 Aggregate를 조율하는 비즈니스 규칙을 담당
 */
class UserMissionDomainService {
    
    /**
     * 사용자가 미션을 완료할 수 있는지 검증
     */
    fun canCompleteMission(user: User, mission: Mission): Boolean {
        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId != user.id) {
            return false
        }
        
        // 미션이 완료 가능한 상태인지 확인
        if (!mission.canBeCompleted()) {
            return false
        }
        
        // 추가적인 비즈니스 규칙들...
        // 예: 하루에 완료 가능한 미션 수 제한, 특정 조건 확인 등
        
        return true
    }
    
    /**
     * 미션 완료 시 추가 보너스 포인트 계산
     */
    fun calculateBonusPoints(user: User, mission: Mission): Int {
        var bonus = 0
        
        // 연속 달성 보너스
        if (user.currentStreak.value >= 7) {
            bonus += (mission.rewardPoints.value * 0.1).toInt() // 10% 보너스
        }
        
        // 레벨에 따른 보너스
        if (user.level.value >= 5) {
            bonus += (mission.rewardPoints.value * 0.05).toInt() // 5% 보너스
        }
        
        // 난이도에 따른 보너스
        bonus += when (mission.difficulty) {
            com.monkeys.spark.domain.vo.mission.MissionDifficulty.HARD -> 10
            com.monkeys.spark.domain.vo.mission.MissionDifficulty.MEDIUM -> 5
            else -> 0
        }
        
        return bonus
    }
}