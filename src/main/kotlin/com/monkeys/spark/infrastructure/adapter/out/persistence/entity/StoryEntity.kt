package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "stories")
class StoryEntity : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
    
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L
    
    @Column(name = "story_type", nullable = false, length = 20)
    var storyType: String = "MISSION_PROOF"
    
    @Column(name = "mission_id", nullable = true)
    var missionId: Long? = null
    
    @Column(name = "mission_title", nullable = true, length = 100)
    var missionTitle: String? = null
    
    @Column(name = "mission_category", nullable = true, length = 20)
    var missionCategory: String? = null
    
    @Column(name = "story_text", columnDefinition = "TEXT", nullable = false)
    var storyText: String = ""
    
    // Comma-separated image URLs
    @Column(columnDefinition = "TEXT")
    var images: String = ""
    
    @Column(nullable = false, length = 100)
    var location: String = ""
    
    // Comma-separated auto tags
    @Column(name = "auto_tags", columnDefinition = "TEXT")
    var autoTags: String = ""
    
    // Comma-separated user tags
    @Column(name = "user_tags", columnDefinition = "TEXT")
    var userTags: String = ""
    
    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean = true
    
    // 좋아요 수 (캐시용) - story_likes 테이블에서 실시간 계산도 가능
    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0
    
    // Comma-separated hash tags
    @Column(name = "hash_tags", columnDefinition = "TEXT")
    var hashTags: String = ""
    
    @Column(name = "comments_count", nullable = false)
    var comments: Int = 0
}