package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "story_comments")
class StoryCommentEntity : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @Column(name = "story_id", nullable = false)
    var storyId: Long = 0L

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L

    @Column(name = "user_name", nullable = false, length = 50)
    var userName: String = ""

    @Column(name = "user_avatar_url")
    var userAvatarUrl: String = ""

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = ""
}