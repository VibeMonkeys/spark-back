package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.RefreshToken
import java.time.LocalDateTime

/**
 * Refresh Token 관리를 위한 Repository Port
 */
interface RefreshTokenRepository {
    
    /**
     * Refresh Token 저장
     */
    fun save(refreshToken: RefreshToken): RefreshToken
    
    /**
     * 토큰으로 조회
     */
    fun findByTokenAndIsActive(token: String, isActive: Boolean = true): RefreshToken?
    
    /**
     * 사용자 ID로 활성 토큰 조회
     */
    fun findByUserIdAndIsActive(userId: String, isActive: Boolean = true): List<RefreshToken>
    
    /**
     * 사용자의 모든 토큰 무효화
     */
    fun revokeAllTokensByUserId(userId: String)
    
    /**
     * 특정 토큰 무효화
     */
    fun revokeTokenByToken(token: String)
    
    /**
     * 만료된 토큰 삭제
     */
    fun deleteExpiredTokens(now: LocalDateTime)
    
    /**
     * 사용자의 활성 토큰 개수 조회
     */
    fun countActiveTokensByUserId(userId: String): Long
}