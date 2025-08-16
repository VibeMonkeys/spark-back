package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.UserId
import java.time.LocalDateTime

/**
 * Refresh Token 도메인 모델
 */
data class RefreshToken(
    val id: Long? = null,
    val userId: UserId,
    val token: String,
    val expiresAt: LocalDateTime,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            userId: UserId,
            token: String,
            expiresAt: LocalDateTime
        ): RefreshToken {
            return RefreshToken(
                userId = userId,
                token = token,
                expiresAt = expiresAt,
                isActive = true,
                createdAt = LocalDateTime.now()
            )
        }
    }
    
    /**
     * 토큰 만료 여부 확인
     */
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
    
    /**
     * 토큰 무효화
     */
    fun revoke(): RefreshToken {
        return this.copy(isActive = false)
    }
}