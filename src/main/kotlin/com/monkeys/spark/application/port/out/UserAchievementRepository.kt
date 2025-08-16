package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.UserAchievement
import com.monkeys.spark.domain.vo.common.UserId

/**
 * 사용자 업적 데이터 접근을 위한 포트 인터페이스
 * 헥사고날 아키텍처의 아웃바운드 포트
 */
interface UserAchievementRepository {

    /**
     * 사용자 업적 저장
     */
    fun save(userAchievement: UserAchievement): UserAchievement

    /**
     * 사용자 ID로 업적 목록 조회
     */
    fun findByUserId(userId: UserId): List<UserAchievement>

    /**
     * 사용자 ID와 업적 타입으로 업적 조회
     */
    fun findByUserIdAndAchievementType(userId: UserId, achievementType: String): UserAchievement?

    /**
     * 사용자의 달성된 업적 개수 조회
     */
    fun countUnlockedByUserId(userId: UserId): Int

    /**
     * 모든 사용자의 업적 통계 조회 (관리자용)
     */
    fun getAchievementStatistics(): Map<String, Int>

}