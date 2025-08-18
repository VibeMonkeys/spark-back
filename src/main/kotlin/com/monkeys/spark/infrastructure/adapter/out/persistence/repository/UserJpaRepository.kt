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
interface UserJpaRepository : JpaRepository<UserEntity, Long> {

    fun findByEmail(email: String): UserEntity?

    fun findByLevel(level: Int): List<UserEntity>

    fun findByCurrentPointsGreaterThan(points: Int): List<UserEntity>

    fun findAllByOrderByCurrentStreakDesc(pageable: Pageable): Page<UserEntity>

    fun findAllByOrderByTotalPointsDesc(pageable: Pageable): Page<UserEntity>

    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long

    fun findByLastLoginAtBefore(lastLoginBefore: LocalDateTime): List<UserEntity>

    @Query("SELECT u.thisMonthPoints FROM UserEntity u WHERE u.id = :userId")
    fun getThisMonthEarnedPoints(@Param("userId") userId: Long): Int?

}