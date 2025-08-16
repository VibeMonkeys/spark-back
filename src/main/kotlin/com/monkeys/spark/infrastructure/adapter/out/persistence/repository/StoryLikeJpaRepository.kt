package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryLikeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import jakarta.transaction.Transactional

@Repository
interface StoryLikeJpaRepository : JpaRepository<StoryLikeEntity, Long> {
    fun existsByStoryIdAndUserId(storyId: String, userId: String): Boolean
    fun findByStoryIdAndUserId(storyId: String, userId: String): StoryLikeEntity?

    @Transactional
    fun deleteByStoryIdAndUserId(storyId: String, userId: String)

    fun countByStoryId(storyId: String): Long
}