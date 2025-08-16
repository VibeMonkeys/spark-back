package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "rewards")
class RewardEntity : BaseEntity() {
    @Id
    var id: String = ""
    
    @Column(nullable = false, length = 100)
    var title: String = ""
    
    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String = ""
    
    @Column(nullable = false, length = 20)
    var category: String = ""
    
    @Column(nullable = false, length = 50)
    var brand: String = ""
    
    @Column(name = "original_price", nullable = false, length = 20)
    var originalPrice: String = ""
    
    @Column(name = "required_points", nullable = false)
    var requiredPoints: Int = 0
    
    @Column(name = "discount_percentage", nullable = false)
    var discountPercentage: Int = 0
    
    @Column(name = "image_url")
    var imageUrl: String = ""
    
    @Column(name = "expiration_days", nullable = false)
    var expirationDays: Int = 30
    
    @Column(name = "is_popular", nullable = false)
    var isPopular: Boolean = false
    
    @Column(name = "is_premium", nullable = false)
    var isPremium: Boolean = false
    
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
    
    @Column(name = "total_exchanged", nullable = false)
    var totalExchanged: Int = 0
    
    // RewardPersistenceAdapter에서 필요한 추가 필드들
    @Column(name = "exchange_count")
    var exchangeCount: Int? = 0
    
    @Column(name = "last_exchanged_at")
    var lastExchangedAt: LocalDateTime? = null
}