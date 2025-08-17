package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 해시태그 통계 JPA 엔티티
 */
@Entity
@Table(
    name = "hashtag_stats",
    indexes = [
        Index(name = "idx_hashtag_stats_hashtag", columnList = "hashtag"),
        Index(name = "idx_hashtag_stats_date", columnList = "date"),
        Index(name = "idx_hashtag_stats_trend_score", columnList = "trendScore"),
        Index(name = "idx_hashtag_stats_hashtag_date", columnList = "hashtag,date"),
        Index(name = "idx_hashtag_stats_daily_count", columnList = "dailyCount"),
        Index(name = "idx_hashtag_stats_weekly_count", columnList = "weeklyCount"),
        Index(name = "idx_hashtag_stats_monthly_count", columnList = "monthlyCount"),
        Index(name = "idx_hashtag_stats_total_count", columnList = "totalCount")
    ]
)
class HashtagStatsEntity : BaseEntity() {
    
    @Id
    @Column(name = "id", length = 36)
    var id: String = ""
    
    @Column(name = "hashtag", nullable = false, length = 50)
    var hashtag: String = ""
    
    @Column(name = "daily_count", nullable = false)
    var dailyCount: Int = 0
    
    @Column(name = "weekly_count", nullable = false)
    var weeklyCount: Int = 0
    
    @Column(name = "monthly_count", nullable = false)
    var monthlyCount: Int = 0
    
    @Column(name = "total_count", nullable = false)
    var totalCount: Int = 0
    
    @Column(name = "last_used_at", nullable = false)
    var lastUsedAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "date", nullable = false)
    var date: LocalDate = LocalDate.now()
    
    @Column(name = "trend_score", nullable = false)
    var trendScore: Double = 0.0
}