package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, String> {

    fun findByEmail(email: String): UserEntity?

    @Query("SELECT u FROM UserEntity u WHERE u.level >= :minLevel ORDER BY u.totalPoints DESC")
    fun findTopUsersByLevel(@Param("minLevel") minLevel: Int): List<UserEntity>

    @Query("SELECT u FROM UserEntity u ORDER BY u.currentStreak DESC LIMIT 10")
    fun findUsersWithLongestStreaks(): List<UserEntity>

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt >= :startDate")
    fun countNewUsersAfter(@Param("startDate") startDate: java.time.LocalDateTime): Long

    @Query("SELECT u FROM UserEntity u WHERE u.totalPoints BETWEEN :minPoints AND :maxPoints")
    fun findUsersByPointsRange(@Param("minPoints") minPoints: Int, @Param("maxPoints") maxPoints: Int): List<UserEntity>

    // UserPersistenceAdapter에서 필요한 추가 메서드들
    fun findByLevel(level: Int): List<UserEntity>

    fun findByCurrentPointsGreaterThan(points: Int): List<UserEntity>

    fun findAllByOrderByCurrentStreakDesc(pageable: Pageable): Page<UserEntity>

    fun findAllByOrderByTotalPointsDesc(pageable: Pageable): Page<UserEntity>

    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long

    fun findByLastLoginAtBefore(lastLoginBefore: LocalDateTime): List<UserEntity>
}