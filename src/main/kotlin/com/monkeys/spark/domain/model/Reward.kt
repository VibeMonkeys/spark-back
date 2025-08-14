package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.reward.*
import java.time.LocalDateTime

// Reward Domain Aggregate Root
data class Reward(
    var id: RewardId,
    var title: RewardTitle,
    var description: RewardDescription,
    var category: RewardCategory,
    var brand: BrandName,
    var originalPrice: OriginalPrice,
    var requiredPoints: Points,
    var discountPercentage: DiscountPercentage,
    var imageUrl: ImageUrl,
    var expirationDays: ExpirationDays,
    var isPopular: Boolean = false,
    var isPremium: Boolean = false,
    var isActive: Boolean = true,
    var totalExchanged: Int = 0,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            title: String,
            description: String,
            category: RewardCategory,
            brand: String,
            originalPrice: String,
            requiredPoints: Int,
            discountPercentage: Int,
            imageUrl: String,
            expirationDays: Int,
            isPremium: Boolean = false
        ): Reward {
            return Reward(
                id = RewardId.generate(),
                title = RewardTitle(title),
                description = RewardDescription(description),
                category = category,
                brand = BrandName(brand),
                originalPrice = OriginalPrice(originalPrice),
                requiredPoints = Points(requiredPoints),
                discountPercentage = DiscountPercentage(discountPercentage),
                imageUrl = ImageUrl(imageUrl),
                expirationDays = ExpirationDays(expirationDays),
                isPremium = isPremium
            )
        }
        
        fun createPremiumSubscription(): Reward {
            return Reward(
                id = RewardId.generate(),
                title = RewardTitle("MONKEYS Premium 1개월"),
                description = RewardDescription("무제한 리롤, 프리미엄 미션, 광고 제거"),
                category = RewardCategory.PREMIUM,
                brand = BrandName("MONKEYS"),
                originalPrice = OriginalPrice("₩4,900"),
                requiredPoints = Points(1200),
                discountPercentage = DiscountPercentage(100), // FREE
                imageUrl = ImageUrl("premium-badge-image-url"),
                expirationDays = ExpirationDays(1), // Immediate activation
                isPremium = true
            )
        }
    }
    
    fun markAsPopular(): Reward {
        isPopular = true
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun unmarkAsPopular(): Reward {
        isPopular = false
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun activate(): Reward {
        isActive = true
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun deactivate(): Reward {
        isActive = false
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun recordExchange(): Reward {
        totalExchanged++
        updatedAt = LocalDateTime.now()
        
        // Auto-mark as popular if exchanged frequently
        if (totalExchanged >= 50 && !isPopular) {
            markAsPopular()
        }
        
        return this
    }
    
    fun updateRequiredPoints(newPoints: Int): Reward {
        requiredPoints = Points(newPoints)
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun updateDiscount(newDiscountPercentage: Int): Reward {
        discountPercentage = DiscountPercentage(newDiscountPercentage)
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun getDiscountText(): String {
        return when {
            discountPercentage.value == 100 -> "FREE"
            discountPercentage.value > 0 -> "${discountPercentage.value}% 할인"
            else -> ""
        }
    }
    
    fun getExpirationText(): String {
        return when {
            expirationDays.value == 1 -> "즉시 적용"
            expirationDays.value <= 7 -> "${expirationDays.value}일"
            expirationDays.value <= 30 -> "${expirationDays.value}일"
            else -> "${expirationDays.value / 30}개월"
        }
    }
}