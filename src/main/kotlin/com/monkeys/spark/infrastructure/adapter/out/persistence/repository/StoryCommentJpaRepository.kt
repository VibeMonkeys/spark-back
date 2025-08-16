package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryCommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryCommentJpaRepository : JpaRepository<StoryCommentEntity, Long> {

    fun findByStoryId(storyId: Long): List<StoryCommentEntity>

    fun findByUserId(userId: Long): List<StoryCommentEntity>

    fun deleteByUserId(userId: Long)
    
}