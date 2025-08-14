package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "story_comments")
class StoryCommentEntity {
    @Id
    var id: String = ""
    
    @Column(name = "story_id", nullable = false)
    var storyId: String = ""
    
    @Column(name = "user_id", nullable = false)
    var userId: String = ""
    
    @Column(name = "user_name", nullable = false, length = 50)
    var userName: String = ""
    
    @Column(name = "user_avatar_url")
    var userAvatarUrl: String = ""
    
    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = ""
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}