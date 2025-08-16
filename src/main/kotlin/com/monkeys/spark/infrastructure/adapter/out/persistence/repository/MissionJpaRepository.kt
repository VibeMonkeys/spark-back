package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.MissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MissionJpaRepository : JpaRepository<MissionEntity, String> {

    fun findByUserId(userId: String): List<MissionEntity>

    fun findByUserIdAndStatus(userId: String, status: String): List<MissionEntity>

    @Query("SELECT m FROM MissionEntity m WHERE m.userId = :userId AND m.status IN :statuses")
    fun findByUserIdAndStatusIn(
        @Param("userId") userId: String,
        @Param("statuses") statuses: List<String>
    ): List<MissionEntity>

    @Query("SELECT m FROM MissionEntity m WHERE m.expiresAt < :currentTime AND m.status = 'ASSIGNED'")
    fun findExpiredMissions(@Param("currentTime") currentTime: LocalDateTime): List<MissionEntity>

    @Query("SELECT m FROM MissionEntity m WHERE m.category = :category AND m.difficulty = :difficulty ORDER BY m.createdAt DESC")
    fun findByCategoryAndDifficulty(
        @Param("category") category: String,
        @Param("difficulty") difficulty: String
    ): List<MissionEntity>

    @Query("SELECT COUNT(m) FROM MissionEntity m WHERE m.status = 'COMPLETED' AND m.completedAt >= :startDate")
    fun countCompletedMissionsAfter(@Param("startDate") startDate: LocalDateTime): Long

    @Query("SELECT m FROM MissionEntity m WHERE m.assignedAt >= :startDate AND m.assignedAt < :endDate ORDER BY m.assignedAt DESC")
    fun findMissionsAssignedBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<MissionEntity>

    // MissionPersistenceAdapter에서 필요한 추가 메서드들
    fun findByUserIdAndCreatedAtBetween(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<MissionEntity>

    fun findByCategory(category: String): List<MissionEntity>

    fun findByDifficulty(difficulty: String): List<MissionEntity>

    fun findByCategoryAndDifficultyAndIdNot(
        category: String,
        difficulty: String,
        excludeId: String
    ): List<MissionEntity>

    fun findByCategoryInAndStatus(categories: List<String>, status: String): List<MissionEntity>

    fun findByStatus(status: String): List<MissionEntity>

    fun findByIsTemplate(isTemplate: Boolean): List<MissionEntity>

    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long

    fun countByUserId(userId: String): Long

    fun countByUserIdAndStatus(userId: String, status: String): Long

    fun countByUserIdAndCategoryAndStatus(userId: String, category: String, status: String): Long

    fun deleteByUserId(userId: String)

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
    fun findAvailableTemplatesForUser(@Param("userId") userId: String): List<MissionEntity>

    // 사용자가 완료했거나 진행 중인 템플릿 미션 ID들
    @Query(
        """
        SELECT DISTINCT m.id FROM MissionEntity m 
        WHERE m.userId = :userId 
        AND m.isTemplate = false
        AND (m.status = 'COMPLETED' OR m.status = 'IN_PROGRESS' OR m.status = 'ASSIGNED')
    """
    )
    fun findUserCompletedOrOngoingTemplateIds(@Param("userId") userId: String): List<String>

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
        @Param("userId") userId: String,
        @Param("limit") limit: Int
    ): List<MissionEntity>

    // 오늘 시작한 미션 개수 조회
    fun countByUserIdAndStartedAtBetween(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): Long
}