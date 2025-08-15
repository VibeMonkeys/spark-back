package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserStatsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 사용자 스탯 JPA 리포지토리
 */
@Repository
interface UserStatsJpaRepository : JpaRepository<UserStatsEntity, String> {
    
    /**
     * 사용자 ID로 스탯 조회
     */
    fun findByUserId(userId: String): UserStatsEntity?

    /**
     * 특정 스탯 값 이상인 사용자들 조회 (랭킹 시스템용)
     */
    @Query("""
        SELECT us FROM UserStatsEntity us 
        WHERE (us.strengthCurrent + us.intelligenceCurrent + us.creativityCurrent + 
               us.sociabilityCurrent + us.adventurousCurrent + us.disciplineCurrent) >= :totalStats
        ORDER BY (us.strengthCurrent + us.intelligenceCurrent + us.creativityCurrent + 
                  us.sociabilityCurrent + us.adventurousCurrent + us.disciplineCurrent) DESC
    """)
    fun findUsersWithTotalStatsGreaterThan(@Param("totalStats") totalStats: Int): List<UserStatsEntity>

    /**
     * 전체 스탯 순 랭킹 조회
     */
    @Query("""
        SELECT us FROM UserStatsEntity us 
        ORDER BY (us.strengthCurrent + us.intelligenceCurrent + us.creativityCurrent + 
                  us.sociabilityCurrent + us.adventurousCurrent + us.disciplineCurrent) DESC
    """)
    fun findAllOrderByTotalStatsDesc(): List<UserStatsEntity>

    /**
     * 특정 스탯별 상위 랭킹 조회
     */
    @Query("SELECT us FROM UserStatsEntity us ORDER BY us.strengthCurrent DESC")
    fun findTopByStrength(): List<UserStatsEntity>

    @Query("SELECT us FROM UserStatsEntity us ORDER BY us.intelligenceCurrent DESC")
    fun findTopByIntelligence(): List<UserStatsEntity>

    @Query("SELECT us FROM UserStatsEntity us ORDER BY us.creativityCurrent DESC")
    fun findTopByCreativity(): List<UserStatsEntity>

    @Query("SELECT us FROM UserStatsEntity us ORDER BY us.sociabilityCurrent DESC")
    fun findTopBySociability(): List<UserStatsEntity>

    @Query("SELECT us FROM UserStatsEntity us ORDER BY us.adventurousCurrent DESC")
    fun findTopByAdventurous(): List<UserStatsEntity>

    @Query("SELECT us FROM UserStatsEntity us ORDER BY us.disciplineCurrent DESC")
    fun findTopByDiscipline(): List<UserStatsEntity>

    /**
     * 사용자 존재 여부 확인
     */
    fun existsByUserId(userId: String): Boolean

    /**
     * 전체 사용자 수 조회
     */
    override fun count(): Long
}