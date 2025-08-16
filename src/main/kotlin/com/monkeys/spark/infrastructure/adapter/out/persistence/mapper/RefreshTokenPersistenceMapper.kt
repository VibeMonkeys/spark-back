package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.RefreshToken
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceMapper {

    fun toEntity(domain: RefreshToken): RefreshTokenEntity {
        return RefreshTokenEntity(
            id = domain.id,
            userId = domain.userId.value.toString(),
            token = domain.token,
            expiresAt = domain.expiresAt,
            isActive = domain.isActive,
            createdAt = domain.createdAt
        )
    }

    fun toDomain(entity: RefreshTokenEntity): RefreshToken {
        return RefreshToken(
            id = entity.id,
            userId = UserId(entity.userId.toLong()),
            token = entity.token,
            expiresAt = entity.expiresAt,
            isActive = entity.isActive,
            createdAt = entity.createdAt
        )
    }

    fun toDomainList(entities: List<RefreshTokenEntity>): List<RefreshToken> {
        return entities.map { toDomain(it) }
    }
}