package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.StoryCommentRepository
import com.monkeys.spark.domain.model.StoryComment
import com.monkeys.spark.domain.vo.common.StoryId
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.StoryCommentPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.StoryCommentJpaRepository
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

    override fun findByStoryId(storyId: StoryId): List<StoryComment> {
        return storyCommentJpaRepository.findByStoryId(storyId.value)
            .map { storyCommentMapper.toDomain(it) }
    }

}