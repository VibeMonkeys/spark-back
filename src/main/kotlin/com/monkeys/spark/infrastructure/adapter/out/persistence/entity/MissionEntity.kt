package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "missions")
class MissionEntity {
    @Id
    var id: String = ""
    
    @Column(name = "user_id", nullable = false)
    var userId: String = ""
    
    @Column(nullable = false, length = 100)
    var title: String = ""
    
    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String = ""
    
    @Column(name = "detailed_description", columnDefinition = "TEXT", nullable = false)
    var detailedDescription: String = ""
    
    @Column(nullable = false, length = 20)
    var category: String = ""
    
    @Column(nullable = false, length = 20)
    var difficulty: String = ""
    
    @Column(nullable = false, length = 20)
    var status: String = "ASSIGNED"
    
    @Column(name = "reward_points", nullable = false)
    var rewardPoints: Int = 0
    
    @Column(name = "estimated_minutes", nullable = false)
    var estimatedMinutes: Int = 0
    
    @Column(name = "image_url")
    var imageUrl: String = ""
    
    // Comma-separated tips
    @Column(columnDefinition = "TEXT")
    var tips: String = ""
    
    // JSON 형태로 저장할 conditions
    @Column(columnDefinition = "TEXT")
    var conditions: String = "{}"
    
    // MissionPersistenceAdapter에서 필요한 추가 필드들
    @Column(name = "completed_count")
    var completedCount: Int? = 0
    
    @Column(name = "available_time_slots")
    var availableTimeSlots: String? = null
    
    @Column(name = "weather_conditions")
    var weatherConditions: String? = null
    
    @Column(name = "location")
    var location: String? = null
    
    @Column(name = "is_template")
    var isTemplate: Boolean = false
    
    @Column(nullable = false)
    var progress: Int = 0
    
    // Statistics fields
    @Column(name = "completed_by", nullable = false)
    var completedBy: Int = 0
    
    @Column(name = "average_rating", nullable = false)
    var averageRating: Double = 0.0
    
    @Column(name = "total_ratings", nullable = false)
    var totalRatings: Int = 0
    
    @Column(name = "average_completion_time", nullable = false)
    var averageCompletionTime: Int = 0
    
    @Column(name = "popularity_score", nullable = false)
    var popularityScore: Double = 0.0
    
    // Timestamps
    @Column(name = "assigned_at", nullable = false)
    var assignedAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null
    
    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null
    
    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime = LocalDateTime.now().plusDays(1)
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
    
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
    
    // Index annotations for better query performance
    companion object {
        // These would typically be defined in migration scripts
        // @Index(name = "idx_mission_user_status", columnList = "user_id, status")
        // @Index(name = "idx_mission_category", columnList = "category")
        // @Index(name = "idx_mission_expires_at", columnList = "expires_at")
    }
}