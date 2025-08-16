package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.domain.vo.achievement.AchievementType
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserAchievementEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 사용자 업적 JPA Repository
 */
@Repository
interface UserAchievementJpaRepository : JpaRepository<UserAchievementEntity, String> {
    
    /**
     * 사용자 ID로 업적 목록 조회 (최신순)
     */
    fun findByUserIdOrderByUnlockedAtDesc(userId: String): List<UserAchievementEntity>
    
    /**
     * 사용자 ID와 업적 타입으로 업적 조회
     */
    fun findByUserIdAndAchievementType(userId: String, achievementType: AchievementType): UserAchievementEntity?
    
    /**
     * 사용자 ID로 달성된 업적 개수 조회 (progress = 100)
     */
    @Query("SELECT COUNT(ua) FROM UserAchievementEntity ua WHERE ua.userId = :userId AND ua.progress = 100")
    fun countUnlockedByUserId(@Param("userId") userId: String): Int
    
    /**
     * 특정 업적 타입의 달성자 수 조회
     */
    @Query("SELECT COUNT(ua) FROM UserAchievementEntity ua WHERE ua.achievementType = :achievementType AND ua.progress = 100")
    fun countByAchievementTypeAndProgress(@Param("achievementType") achievementType: AchievementType): Int
    
    /**
     * 모든 업적 타입별 달성자 수 통계 조회
     */
    @Query("""
        SELECT ua.achievementType as achievementType, COUNT(ua) as count 
        FROM UserAchievementEntity ua 
        WHERE ua.progress = 100 
        GROUP BY ua.achievementType
    """)
    fun getAchievementStatistics(): List<AchievementStatistic>
    
    /**
     * 업적 통계 결과를 위한 프로젝션 인터페이스
     */
    interface AchievementStatistic {
        fun getAchievementType(): AchievementType
        fun getCount(): Long
    }
    
    /**
     * 사용자가 달성한 업적이 존재하는지 확인
     */
    fun existsByUserIdAndAchievementType(userId: String, achievementType: AchievementType): Boolean
    
    /**
     * 알림이 전송되지 않은 업적들 조회
     */
    @Query("SELECT ua FROM UserAchievementEntity ua WHERE ua.isNotified = false AND ua.progress = 100")
    fun findUnnotifiedAchievements(): List<UserAchievementEntity>
}