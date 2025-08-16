package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.StoryCommentRepository
import com.monkeys.spark.domain.model.StoryComment
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.StoryCommentJpaRepository
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.StoryCommentPersistenceMapper
import org.springframework.stereotype.Component

@Component
class StoryCommentPersistenceAdapter(
    private val storyCommentJpaRepository: StoryCommentJpaRepository,
    private val storyCommentMapper: StoryCommentPersistenceMapper
) : StoryCommentRepository {

    override fun save(comment: StoryComment): StoryComment {
        val entity = storyCommentMapper.toEntity(comment)
        val savedEntity = storyCommentJpaRepository.save(entity)
        return storyCommentMapper.toDomain(savedEntity)
    }

    override fun findById(commentId: String): StoryComment? {
        return storyCommentJpaRepository.findById(commentId)
            .map { storyCommentMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByStoryId(storyId: StoryId): List<StoryComment> {
        return storyCommentJpaRepository.findByStoryId(storyId.value)
            .map { storyCommentMapper.toDomain(it) }
    }

    override fun findByUserId(userId: UserId): List<StoryComment> {
        return storyCommentJpaRepository.findByUserId(userId.value)
            .map { storyCommentMapper.toDomain(it) }
    }

    override fun countByStoryId(storyId: StoryId): Int {
        return storyCommentJpaRepository.countByStoryId(storyId.value).toInt()
    }

    override fun deleteById(commentId: String) {
        storyCommentJpaRepository.deleteById(commentId)
    }

    override fun deleteByStoryId(storyId: StoryId) {
        storyCommentJpaRepository.deleteByStoryId(storyId.value)
    }

    override fun deleteByUserId(userId: UserId) {
        storyCommentJpaRepository.deleteByUserId(userId.value)
    }
}