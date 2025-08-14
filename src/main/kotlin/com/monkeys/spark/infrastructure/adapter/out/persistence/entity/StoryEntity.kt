package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "stories")
class StoryEntity {
    @Id
    var id: String = ""
    
    @Column(name = "user_id", nullable = false)
    var userId: String = ""
    
    @Column(name = "mission_id", nullable = false)
    var missionId: String = ""
    
    @Column(name = "mission_title", nullable = false, length = 100)
    var missionTitle: String = ""
    
    @Column(name = "mission_category", nullable = false, length = 20)
    var missionCategory: String = ""
    
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
    
    @Column(name = "likes_count", nullable = false)
    var likes: Int = 0
    
    // StoryPersistenceAdapter에서 필요한 추가 필드들
    @Column(name = "like_count")
    var likeCount: Int? = 0
    
    // Comma-separated hash tags
    @Column(name = "hash_tags", columnDefinition = "TEXT")
    var hashTags: String = ""
    
    @Column(name = "comments_count", nullable = false)
    var comments: Int = 0
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
    
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}