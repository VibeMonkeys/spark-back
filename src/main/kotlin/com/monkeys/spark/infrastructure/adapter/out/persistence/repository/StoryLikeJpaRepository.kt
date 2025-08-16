package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryLikeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryLikeJpaRepository : JpaRepository<StoryLikeEntity, Long> {

    fun existsByStoryIdAndUserId(storyId: Long, userId: Long): Boolean

    fun deleteByStoryIdAndUserId(storyId: Long, userId: Long)

}