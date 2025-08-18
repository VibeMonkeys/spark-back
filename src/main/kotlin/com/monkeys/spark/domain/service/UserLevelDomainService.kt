package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.user.UserLevelInfo

/**
 * 사용자 레벨 계산 관련 순수 도메인 서비스
 * UserLevelInfo VO를 활용하여 레벨 관련 계산 제공
 */
class UserLevelDomainService {
    
    /**
     * 다음 레벨까지의 진행도 계산 (0-100%)
     */
    fun calculateProgressToNextLevel(currentPoints: Int, currentLevel: Int): Int {
        val levelInfo = UserLevelInfo.fromPoints(Points(currentPoints))
        return levelInfo.progressPercentage.toInt()
    }
    
    /**
     * 다음 레벨까지 필요한 포인트 계산
     */
    fun calculatePointsToNextLevel(currentPoints: Int, currentLevel: Int): Int {
        val levelInfo = UserLevelInfo.fromPoints(Points(currentPoints))
        return levelInfo.pointsToNext
    }
    
    /**
     * 현재 포인트를 기반으로 적절한 레벨 계산
     */
    fun calculateLevelFromPoints(totalPoints: Int): Int {
        val levelInfo = UserLevelInfo.fromPoints(Points(totalPoints))
        return levelInfo.level.value
    }
}