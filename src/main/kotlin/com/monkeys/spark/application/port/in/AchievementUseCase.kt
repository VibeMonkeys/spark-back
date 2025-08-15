package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.common.UserId

/**
 * 업적 시스템 Use Case 인터페이스
 */
interface AchievementUseCase {
    
    /**
     * 사용자의 모든 업적 조회 (달성된 것과 진행 중인 것 포함)
     */
    fun getUserAchievements(userId: UserId): List<UserAchievement>
    
    /**
     * 사용자의 달성한 업적 개수 조회
     */
    fun getUserAchievementCount(userId: UserId): Int
    
    /**
     * 업적 통계 조회 (관리자용)
     */
    fun getAchievementStatistics(): Map<String, Int>
}