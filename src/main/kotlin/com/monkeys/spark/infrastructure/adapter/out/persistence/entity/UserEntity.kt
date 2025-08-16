package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserEntity : BaseEntity() {
    @Id
    var id: String = ""
    
    @Column(unique = true, nullable = false)
    var email: String = ""
    
    @Column(nullable = false, length = 50)
    var name: String = ""
    
    @Column(nullable = false)
    var password: String = ""
    
    @Column(name = "avatar_url")
    var avatarUrl: String = ""
    
    @Column(nullable = false)
    var level: Int = 1
    
    @Column(name = "level_title", nullable = false, length = 20)
    var levelTitle: String = "BEGINNER"
    
    @Column(name = "current_points", nullable = false)
    var currentPoints: Int = 0
    
    @Column(name = "total_points", nullable = false)
    var totalPoints: Int = 0
    
    @Column(name = "current_streak", nullable = false)
    var currentStreak: Int = 0
    
    @Column(name = "longest_streak", nullable = false)
    var longestStreak: Int = 0
    
    @Column(name = "completed_missions", nullable = false)
    var completedMissions: Int = 0
    
    @Column(name = "total_days", nullable = false)
    var totalDays: Int = 0
    
    // JSON 형태로 저장할 preferences
    @Column(columnDefinition = "TEXT")
    var preferences: String = "{}"
    
    // Statistics fields
    @Column(name = "this_month_points", nullable = false)
    var thisMonthPoints: Int = 0
    
    @Column(name = "this_month_missions", nullable = false)
    var thisMonthMissions: Int = 0
    
    @Column(name = "average_rating", nullable = false)
    var averageRating: Double = 0.0
    
    @Column(name = "total_ratings", nullable = false)
    var totalRatings: Int = 0
    
    // Category statistics - JSON 형태로 저장
    @Column(name = "category_stats", columnDefinition = "TEXT")
    var categoryStats: String = "{}"
    
    // UserPersistenceAdapter에서 필요한 추가 필드들
    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null
    
    @Column(name = "last_completed_date")
    var lastCompletedDate: LocalDateTime? = null
    
    // 프로필 관리 필드
    @Column(name = "bio", length = 200)
    var bio: String? = null
}