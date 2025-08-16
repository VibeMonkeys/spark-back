package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, Long> {

    fun findByTokenAndIsActive(token: String, isActive: Boolean = true): RefreshTokenEntity?

    fun findByUserIdAndIsActive(userId: String, isActive: Boolean = true): List<RefreshTokenEntity>

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.isActive = false WHERE r.userId = :userId")
    fun revokeAllTokensByUserId(@Param("userId") userId: String)

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.isActive = false WHERE r.token = :token")
    fun revokeTokenByToken(@Param("token") token: String)

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt < :now")
    fun deleteExpiredTokens(@Param("now") now: LocalDateTime)

    @Query("SELECT COUNT(r) FROM RefreshTokenEntity r WHERE r.userId = :userId AND r.isActive = true")
    fun countActiveTokensByUserId(@Param("userId") userId: String): Long
}