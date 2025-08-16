package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.StoryRepository
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.StoryJpaRepository
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.StoryLikeJpaRepository
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryLikeEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.StoryPersistenceMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class StoryPersistenceAdapter(
    private val storyJpaRepository: StoryJpaRepository,
    private val storyLikeJpaRepository: StoryLikeJpaRepository,
    private val storyMapper: StoryPersistenceMapper
) : StoryRepository {

    override fun save(story: Story): Story {
        val entity = storyMapper.toEntity(story)
        val savedEntity = storyJpaRepository.save(entity)
        return storyMapper.toDomain(savedEntity)
    }

    override fun findById(id: StoryId): Story? {
        return storyJpaRepository.findById(id.value)
            .map { storyMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByUserId(userId: UserId): List<Story> {
        return storyJpaRepository.findByUserId(userId.value)
            .map { storyMapper.toDomain(it) }
    }

    override fun findByMissionId(missionId: MissionId): List<Story> {
        return storyJpaRepository.findByMissionId(missionId.value)
            .map { storyMapper.toDomain(it) }
    }

    override fun findPublicStories(page: Int, size: Int): List<Story> {
        val pageable = PageRequest.of(page, size)
        return storyJpaRepository.findPublicStoriesOrderByCreatedDesc(pageable)
            .map { storyMapper.toDomain(it) }
    }

    override fun findPopularStories(limit: Int): List<Story> {
        val pageable = PageRequest.of(0, limit)
        return storyJpaRepository.findAll()
            .sortedByDescending { it.likeCount ?: 0 }
            .take(limit)
            .map { storyMapper.toDomain(it) }
    }

    override fun findRecentStories(limit: Int): List<Story> {
        val pageable = PageRequest.of(0, limit)
        return storyJpaRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map { storyMapper.toDomain(it) }
    }

    override fun findByMissionCategory(category: MissionCategory): List<Story> {
        return storyJpaRepository.findByMissionCategory(category.name)
            .map { storyMapper.toDomain(it) }
    }

    override fun findByLocation(location: Location): List<Story> {
        return storyJpaRepository.findByLocationContaining(location.value)
            .map { storyMapper.toDomain(it) }
    }

    override fun findByHashTag(hashTag: HashTag): List<Story> {
        return storyJpaRepository.findByHashTagsContaining(hashTag.value)
            .map { storyMapper.toDomain(it) }
    }

    override fun searchByContent(keyword: String): List<Story> {
        return storyJpaRepository.findByStoryTextContainingIgnoreCase(keyword)
            .map { storyMapper.toDomain(it) }
    }

    override fun findFeedStories(userId: UserId, page: Int, size: Int): List<Story> {
        // 임시 구현 - 전체 공개 스토리로 대체
        return findPublicStories(page, size)
    }

    override fun findByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Story> {
        return storyJpaRepository.findByCreatedAtBetween(startDate, endDate)
            .map { storyMapper.toDomain(it) }
    }

    override fun likeStory(storyId: StoryId, userId: UserId): Story? {
        return storyJpaRepository.findById(storyId.value).map { entity ->
            if (!isLikedByUser(storyId, userId)) {
                // StoryLikeEntity 생성
                val likeEntity = StoryLikeEntity().apply {
                    this.storyId = storyId.value
                    this.userId = userId.value
                    this.createdAt = LocalDateTime.now()
                }
                storyLikeJpaRepository.save(likeEntity)

                // 좋아요 수 증가
                entity.likeCount = entity.likeCount + 1
                storyJpaRepository.save(entity)
            }
            storyMapper.toDomain(entity)
        }.orElse(null)
    }

    override fun unlikeStory(storyId: StoryId, userId: UserId): Story? {
        return storyJpaRepository.findById(storyId.value).map { entity ->
            if (isLikedByUser(storyId, userId)) {
                // StoryLikeEntity 삭제
                storyLikeJpaRepository.deleteByStoryIdAndUserId(storyId.value, userId.value)

                // 좋아요 수 감소
                entity.likeCount = maxOf(0, entity.likeCount - 1)
                storyJpaRepository.save(entity)
            }
            storyMapper.toDomain(entity)
        }.orElse(null)
    }

    override fun isLikedByUser(storyId: StoryId, userId: UserId): Boolean {
        return storyLikeJpaRepository.existsByStoryIdAndUserId(storyId.value, userId.value)
    }

    override fun deleteById(storyId: StoryId) {
        storyJpaRepository.deleteById(storyId.value)
    }

    override fun deleteByUserId(userId: UserId) {
        storyJpaRepository.deleteByUserId(userId.value)
    }

    override fun deleteByMissionId(missionId: MissionId) {
        storyJpaRepository.deleteByMissionId(missionId.value)
    }
}