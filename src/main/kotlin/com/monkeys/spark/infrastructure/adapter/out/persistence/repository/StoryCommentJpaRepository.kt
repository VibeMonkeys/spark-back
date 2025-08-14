package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryCommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StoryCommentJpaRepository : JpaRepository<StoryCommentEntity, String> {
    
    fun findByStoryId(storyId: String): List<StoryCommentEntity>
    
    fun findByUserId(userId: String): List<StoryCommentEntity>
    
    @Query("SELECT COUNT(c) FROM StoryCommentEntity c WHERE c.storyId = :storyId")
    fun countByStoryId(@Param("storyId") storyId: String): Long
    
    fun deleteByStoryId(storyId: String)
    
    fun deleteByUserId(userId: String)
}