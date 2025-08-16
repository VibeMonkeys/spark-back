package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_rewards")
class UserRewardEntity : BaseEntity() {
    @Id
    var id: String = ""
    
    @Column(name = "user_id", nullable = false)
    var userId: String = ""
    
    @Column(name = "reward_id", nullable = false)
    var rewardId: String = ""
    
    @Column(name = "reward_title", nullable = false)
    var rewardTitle: String = ""
    
    @Column(name = "reward_brand", nullable = false)
    var rewardBrand: String = ""
    
    @Column(name = "points_used", nullable = false)
    var pointsUsed: Int = 0
    
    @Column(name = "exchange_code", nullable = false, unique = true)
    var exchangeCode: String = ""
    
    @Column(nullable = false, length = 20)
    var status: String = "AVAILABLE"
    
    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null
    
    // UserRewardPersistenceAdapter에서 필요한 추가 필드들
    @Column(name = "exchanged_at")
    var exchangedAt: LocalDateTime? = null
}