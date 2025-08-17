package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.MissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MissionJpaRepository : JpaRepository<MissionEntity, Long> {

    fun findByUserId(userId: Long): List<MissionEntity>

    fun findByUserIdAndStatus(userId: Long, status: String): List<MissionEntity>

    @Query("SELECT m FROM MissionEntity m WHERE m.expiresAt < :currentTime AND m.status IN ('ASSIGNED', 'IN_PROGRESS')")
    fun findExpiredMissions(@Param("currentTime") currentTime: LocalDateTime): List<MissionEntity>

    fun findByUserIdAndCreatedAtBetween(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<MissionEntity>

    fun findByCategory(category: String): List<MissionEntity>

    fun findByDifficulty(difficulty: String): List<MissionEntity>

    fun findByCategoryAndDifficultyAndIdNot(
        category: String,
        difficulty: String,
        excludeId: Long
    ): List<MissionEntity>

    fun findByCategoryInAndStatus(categories: List<String>, status: String): List<MissionEntity>

    fun findByStatus(status: String): List<MissionEntity>

    fun findByIsTemplate(isTemplate: Boolean): List<MissionEntity>

    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long

    fun countByUserId(userId: Long): Long

    fun countByUserIdAndStatus(userId: Long, status: String): Long

    fun countByUserIdAndCategoryAndStatus(userId: Long, category: String, status: String): Long

    fun deleteByUserId(userId: Long)

    // 사용자가 아직 시도하지 않은 템플릿 미션들 조회
    @Query(
        """
        SELECT m FROM MissionEntity m 
        WHERE m.isTemplate = true 
        AND m.id NOT IN (
            SELECT DISTINCT m2.id FROM MissionEntity m2 
            WHERE m2.userId = :userId 
            AND m2.isTemplate = false
            AND (m2.status = 'COMPLETED' OR m2.status = 'IN_PROGRESS' OR m2.status = 'ASSIGNED')
        )
    """
    )
    fun findAvailableTemplatesForUser(@Param("userId") userId: Long): List<MissionEntity>

    // 사용자가 완료했거나 진행 중인 템플릿 미션 ID들
    @Query(
        """
        SELECT DISTINCT m.id FROM MissionEntity m 
        WHERE m.userId = :userId 
        AND m.isTemplate = false
        AND (m.status = 'COMPLETED' OR m.status = 'IN_PROGRESS' OR m.status = 'ASSIGNED')
    """
    )
    fun findUserCompletedOrOngoingTemplateIds(@Param("userId") userId: Long): List<String>

    // 랜덤으로 미션 조회 (Native Query 사용)
    @Query(
        value = """
        SELECT * FROM missions m 
        WHERE m.is_template = true 
        AND m.id NOT IN (
            SELECT DISTINCT m2.id FROM missions m2 
            WHERE m2.user_id = :userId 
            AND m2.is_template = false
            AND (m2.status = 'COMPLETED' OR m2.status = 'IN_PROGRESS' OR m2.status = 'ASSIGNED')
        )
        ORDER BY RANDOM() 
        LIMIT :limit
    """, nativeQuery = true
    )
    fun findRandomAvailableTemplatesForUser(
        @Param("userId") userId: Long,
        @Param("limit") limit: Int
    ): List<MissionEntity>

    // 오늘 시작한 미션 개수 조회
    fun countByUserIdAndStartedAtBetween(userId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Long

}