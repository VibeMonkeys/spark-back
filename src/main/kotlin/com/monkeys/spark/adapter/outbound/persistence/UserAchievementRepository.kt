package com.monkeys.spark.adapter.outbound.persistence

import com.monkeys.spark.domain.vo.achievement.AchievementType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 사용자 업적 JPA Repository
 */
@Repository
interface UserAchievementRepository : JpaRepository<UserAchievementEntity, Long> {
    
    /**
     * 사용자 ID로 업적 목록 조회
     */
    fun findByUserId(userId: String): List<UserAchievementEntity>
    
    /**
     * 사용자 ID와 업적 타입으로 업적 조회
     */
    fun findByUserIdAndAchievementType(userId: String, achievementType: AchievementType): UserAchievementEntity?
    
    /**
     * 사용자의 달성된 업적 개수 조회 (진행도 100% 이상)
     */
    @Query("SELECT COUNT(ua) FROM UserAchievementEntity ua WHERE ua.userId = :userId AND ua.progress >= 100")
    fun countUnlockedByUserId(@Param("userId") userId: String): Int
    
    /**
     * 업적별 달성 사용자 수 통계
     */
    @Query("""
        SELECT ua.achievementType as achievementType, COUNT(ua) as count 
        FROM UserAchievementEntity ua 
        WHERE ua.progress >= 100 
        GROUP BY ua.achievementType
    """)
    fun getAchievementStatistics(): List<AchievementStatistics>
    
    /**
     * 사용자의 최근 달성한 업적들 (알림용)
     */
    @Query("""
        SELECT ua FROM UserAchievementEntity ua 
        WHERE ua.userId = :userId 
        AND ua.progress >= 100 
        AND ua.isNotified = false 
        ORDER BY ua.unlockedAt DESC
    """)
    fun findRecentUnnotifiedAchievements(@Param("userId") userId: String): List<UserAchievementEntity>
}

/**
 * 업적 통계를 위한 프로젝션 인터페이스
 */
interface AchievementStatistics {
    fun getAchievementType(): AchievementType
    fun getCount(): Long
}