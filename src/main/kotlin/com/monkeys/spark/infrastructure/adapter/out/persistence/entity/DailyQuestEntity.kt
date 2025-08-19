package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*

/**
 * 일일 퀘스트 템플릿 엔티티
 * "삶을 게임처럼 즐겨라!" - 매일 반복되는 기본 퀘스트들
 */
@Entity
@Table(name = "daily_quests")
class DailyQuestEntity : BaseEntity() {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
    
    @Column(name = "quest_type", nullable = false, length = 50, unique = true)
    var questType: String = ""
    
    @Column(nullable = false, length = 100)
    var title: String = ""
    
    @Column(nullable = false, length = 500)
    var description: String = ""
    
    @Column(nullable = false, length = 20)
    var icon: String = ""
    
    @Column(name = "quest_order", nullable = false)
    var order: Int = 0
    
    @Column(name = "reward_points", nullable = false)
    var rewardPoints: Int = 5
    
    @Column(name = "stat_reward", nullable = false, length = 20)
    var statReward: String = "DISCIPLINE"
    
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
    
    // Index annotations for better query performance
    companion object {
        // @Index(name = "idx_daily_quest_type", columnList = "quest_type")
        // @Index(name = "idx_daily_quest_active", columnList = "is_active")
        // @Index(name = "idx_daily_quest_order", columnList = "quest_order")
    }
}