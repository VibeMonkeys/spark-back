package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
data class RefreshTokenEntity(
    @Id
    val id: String = java.util.UUID.randomUUID().toString(),
    
    @Column(name = "user_id", nullable = false)
    val userId: String,
    
    @Column(name = "token", nullable = false, unique = true, length = 500)
    val token: String,
    
    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true
) {
    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
    
    fun revoke(): RefreshTokenEntity = copy(isActive = false)
}