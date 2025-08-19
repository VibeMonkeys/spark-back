package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * 사용자별 일일 퀘스트 요약 엔티티
 * "삶을 게임처럼 즐겨라!" - 하루의 퀘스트 전체 현황과 보상 관리
 */
@Entity
@Table(
    name = "daily_quest_summary",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_daily_quest_summary_user_date",
            columnNames = ["user_id", "summary_date"]
        )
    ]
)
class DailyQuestSummaryEntity : BaseEntity() {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
    
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L
    
    @Column(name = "summary_date", nullable = false)
    var summaryDate: LocalDate = LocalDate.now()
    
    @Column(name = "completed_count", nullable = false)
    var completedCount: Int = 0
    
    @Column(name = "total_count", nullable = false)
    var totalCount: Int = 4 // 기본 4개 퀘스트
    
    @Column(name = "completion_percentage", nullable = false)
    var completionPercentage: Int = 0
    
    /**
     * 획득한 특수 보상들 (JSON 형태로 저장)
     * 예: ["BRONZE", "SILVER", "GOLD", "PLATINUM"]
     */
    @Column(name = "special_rewards_earned", columnDefinition = "TEXT")
    var specialRewardsEarned: String = "[]"
    
    @Column(name = "base_reward_points", nullable = false)
    var baseRewardPoints: Int = 0
    
    @Column(name = "special_reward_points", nullable = false)
    var specialRewardPoints: Int = 0
    
    @Column(name = "total_reward_points", nullable = false)
    var totalRewardPoints: Int = 0
    
    @Column(name = "total_stat_reward", nullable = false)
    var totalStatReward: Int = 0
    
    /**
     * 게임화된 상태 메시지
     */
    @Column(name = "status_message", columnDefinition = "TEXT")
    var statusMessage: String = ""
    
    // Index annotations for better query performance
    companion object {
        // @Index(name = "idx_daily_summary_user_date", columnList = "user_id, summary_date")
        // @Index(name = "idx_daily_summary_date", columnList = "summary_date")
        // @Index(name = "idx_daily_summary_completion", columnList = "completion_percentage")
        // @Index(name = "idx_daily_summary_user_completion", columnList = "user_id, completion_percentage")
    }
}