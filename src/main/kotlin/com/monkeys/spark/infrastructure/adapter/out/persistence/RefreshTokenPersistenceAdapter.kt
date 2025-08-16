package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.RefreshTokenRepository
import com.monkeys.spark.domain.model.RefreshToken
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.RefreshTokenPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.RefreshTokenJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional
class RefreshTokenPersistenceAdapter(
    private val jpaRepository: RefreshTokenJpaRepository,
    private val mapper: RefreshTokenPersistenceMapper
) : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken): RefreshToken {
        val entity = mapper.toEntity(refreshToken)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findByTokenAndIsActive(token: String, isActive: Boolean): RefreshToken? {
        val entity = jpaRepository.findByTokenAndIsActive(token, isActive)
        return entity?.let { mapper.toDomain(it) }
    }

    override fun findByUserIdAndIsActive(userId: String, isActive: Boolean): List<RefreshToken> {
        val entities = jpaRepository.findByUserIdAndIsActive(userId, isActive)
        return mapper.toDomainList(entities)
    }

    override fun revokeAllTokensByUserId(userId: String) {
        jpaRepository.revokeAllTokensByUserId(userId)
    }

    override fun revokeTokenByToken(token: String) {
        jpaRepository.revokeTokenByToken(token)
    }

    override fun deleteExpiredTokens(now: LocalDateTime) {
        jpaRepository.deleteExpiredTokens(now)
    }

    override fun countActiveTokensByUserId(userId: String): Long {
        return jpaRepository.countActiveTokensByUserId(userId)
    }
}