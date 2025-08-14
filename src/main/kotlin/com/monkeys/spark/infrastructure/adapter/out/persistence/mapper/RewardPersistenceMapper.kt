package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.Reward
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.RewardEntity
import org.springframework.stereotype.Component

@Component
class RewardPersistenceMapper {
    
    fun toEntity(domain: Reward): RewardEntity {
        val entity = RewardEntity()
        entity.id = domain.id.value
        entity.title = domain.title.value
        entity.description = domain.description.value
        entity.category = domain.category.name
        entity.brand = domain.brand.value
        entity.originalPrice = domain.originalPrice.value
        entity.requiredPoints = domain.requiredPoints.value
        entity.discountPercentage = domain.discountPercentage.value
        entity.imageUrl = domain.imageUrl.value
        entity.expirationDays = domain.expirationDays.value
        entity.isPopular = domain.isPopular
        entity.isPremium = domain.isPremium
        entity.isActive = domain.isActive
        entity.totalExchanged = domain.totalExchanged
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        
        return entity
    }
    
    fun toDomain(entity: RewardEntity): Reward {
        return Reward(
            id = RewardId(entity.id),
            title = RewardTitle(entity.title),
            description = RewardDescription(entity.description),
            category = RewardCategory.valueOf(entity.category),
            brand = BrandName(entity.brand),
            originalPrice = OriginalPrice(entity.originalPrice),
            requiredPoints = Points(entity.requiredPoints),
            discountPercentage = DiscountPercentage(entity.discountPercentage),
            imageUrl = ImageUrl(entity.imageUrl),
            expirationDays = ExpirationDays(entity.expirationDays),
            isPopular = entity.isPopular,
            isPremium = entity.isPremium,
            isActive = entity.isActive,
            totalExchanged = entity.totalExchanged,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}