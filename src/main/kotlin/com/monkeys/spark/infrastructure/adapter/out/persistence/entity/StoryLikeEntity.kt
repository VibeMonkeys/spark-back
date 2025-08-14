package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "story_likes",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_story_user_like", columnNames = ["story_id", "user_id"])
    ]
)
class StoryLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    
    @Column(name = "story_id", nullable = false)
    var storyId: String = ""
    
    @Column(name = "user_id", nullable = false)
    var userId: String = ""
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}