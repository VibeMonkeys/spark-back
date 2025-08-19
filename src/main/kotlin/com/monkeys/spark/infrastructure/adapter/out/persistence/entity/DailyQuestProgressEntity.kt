package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 사용자별 일일 퀘스트 진행 상황 엔티티
 * 특정 날짜의 개별 퀘스트 완료 여부를 추적
 */
@Entity
@Table(
    name = "daily_quest_progress",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_daily_quest_progress_user_quest_date",
            columnNames = ["user_id", "daily_quest_id", "quest_date"]
        )
    ]
)
class DailyQuestProgressEntity : BaseEntity() {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
    
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L
    
    @Column(name = "daily_quest_id", nullable = false)
    var dailyQuestId: Long = 0L
    
    @Column(name = "quest_type", nullable = false, length = 50)
    var questType: String = ""
    
    @Column(name = "quest_date", nullable = false)
    var questDate: LocalDate = LocalDate.now()
    
    @Column(name = "is_completed", nullable = false)
    var isCompleted: Boolean = false
    
    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null
    
    // Index annotations for better query performance
    companion object {
        // @Index(name = "idx_daily_progress_user_date", columnList = "user_id, quest_date")
        // @Index(name = "idx_daily_progress_user_completed", columnList = "user_id, is_completed")
        // @Index(name = "idx_daily_progress_date", columnList = "quest_date")
        // @Index(name = "idx_daily_progress_quest_type", columnList = "quest_type")
    }
}