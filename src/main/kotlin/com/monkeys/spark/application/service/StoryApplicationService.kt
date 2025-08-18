package com.monkeys.spark.application.service

import com.monkeys.spark.application.dto.*
import com.monkeys.spark.application.port.`in`.StoryUseCase
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.SearchStoriesQuery
import com.monkeys.spark.application.port.`in`.query.StoryFeedQuery
import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.application.port.out.StoryCommentRepository
import com.monkeys.spark.application.port.out.StoryRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.exception.BusinessRuleViolationException
import com.monkeys.spark.domain.exception.MissionNotFoundException
import com.monkeys.spark.domain.exception.StoryNotFoundException
import com.monkeys.spark.domain.exception.UserNotFoundException
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.model.StoryComment
import com.monkeys.spark.domain.service.StoryMissionDomainService
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.StoryId
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.domain.vo.story.StoryText
import com.monkeys.spark.domain.vo.story.StoryType
import com.monkeys.spark.application.coordinator.StoryHashtagCoordinator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StoryApplicationService(
    private val storyRepository: StoryRepository,
    private val storyCommentRepository: StoryCommentRepository,
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository,
    private val storyMissionDomainService: StoryMissionDomainService,
    private val storyHashtagCoordinator: StoryHashtagCoordinator
) : StoryUseCase {

    override fun createStory(command: CreateStoryCommand): Story {
        val userId = UserId(command.userId)
        val missionId = MissionId(command.missionId)

        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        // 미션 조회 및 검증
        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(command.missionId.toString())

        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId != userId) {
            throw BusinessRuleViolationException("Mission does not belong to user: ${command.userId}")
        }

        // 스토리 생성
        val story = Story.createMissionProof(
            userId = userId,
            missionId = missionId,
            missionTitle = mission.title,
            missionCategory = mission.category,
            storyText = command.storyText,
            images = command.images,
            location = command.location,
            userTags = command.userTags,
            isPublic = command.isPublic
        )

        // 스토리 저장
        val savedStory = storyRepository.save(story)

        // 해시태그 통계 업데이트
        val allTags = (savedStory.userTags + savedStory.autoTags).distinct()
        storyHashtagCoordinator.updateHashtagStatsForStory(allTags)

        // 도메인 서비스를 통한 미션 완료 처리
        if (storyMissionDomainService.canCompleteMissionFromStory(user, mission)) {
            val (updatedUser, completedMission) = storyMissionDomainService.completeMissionFromStory(user, mission)

            // 변경사항 저장
            missionRepository.save(completedMission)
            userRepository.save(updatedUser)
        }

        return savedStory
    }

    override fun getStory(storyId: StoryId): Story? {
        return storyRepository.findById(storyId)
    }

    override fun getStoryFeed(query: StoryFeedQuery): List<StoryFeedItem> {
        val stories = when (query.sortBy) {
            "latest" -> storyRepository.findPublicStories(query.page, query.size)
            "popular" -> storyRepository.findPopularStories(query.size)
            else -> storyRepository.findPublicStories(query.page, query.size)
        }

        return buildStoryFeedItems(stories, query.userId?.let { UserId(it) })
    }

    override fun getStoryFeedWithCursor(
        userId: String?,
        cursor: Long?,
        size: Int,
        isNext: Boolean
    ): List<StoryFeedItem> {
        val userIdVO = userId?.toLongOrNull()?.let { UserId(it) }
        val stories = storyRepository.findFeedStoriesWithCursor(userIdVO, cursor, size, isNext)
        return buildStoryFeedItems(stories, userIdVO)
    }

    private fun buildStoryFeedItems(
        stories: List<Story>,
        userId: UserId?
    ): List<StoryFeedItem> {
        return stories.mapNotNull { story ->
            try {
                val user = userRepository.findById(story.userId) ?: return@mapNotNull null

                val isLiked = userId?.value?.let {
                    storyRepository.isLikedByUser(story.id, UserId(it))
                } ?: false

                StoryFeedItem(
                    storyId = story.id,
                    user = StoryUser(
                        userId = user.id,
                        name = user.name,
                        avatarUrl = user.avatarUrl,
                        level = user.level,
                        levelTitle = user.levelTitle
                    ),
                    mission = StoryMission(
                        missionId = story.missionId,
                        title = story.missionTitle,
                        category = story.missionCategory
                    ),
                    content = StoryContent(
                        storyText = story.storyText,
                        images = story.images,
                        tags = story.userTags
                    ),
                    interactions = StoryInteractions(
                        likes = story.likes,
                        comments = story.comments,
                        isLikedByCurrentUser = isLiked
                    ),
                    timeAgo = story.getTimeAgo(),
                    location = story.location
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun getUserStories(
        userId: UserId,
        page: Int,
        size: Int
    ): List<Story> {
        return storyRepository.findByUserId(userId)
    }

    override fun getUserStoriesWithCursor(
        userId: UserId,
        cursor: Long?,
        size: Int,
        isNext: Boolean
    ): List<Story> {
        return storyRepository.findUserStoriesWithCursor(userId, cursor, size, isNext)
    }

    override fun likeStory(command: LikeStoryCommand): Story {
        val storyId = StoryId(command.storyId)
        val userId = UserId(command.userId)

        return storyRepository.likeStory(storyId, userId)
            ?: throw StoryNotFoundException(command.storyId)
    }

    override fun unlikeStory(command: UnlikeStoryCommand): Story {
        val storyId = StoryId(command.storyId)
        val userId = UserId(command.userId)

        return storyRepository.unlikeStory(storyId, userId)
            ?: throw StoryNotFoundException(command.storyId)
    }

    override fun addComment(command: AddCommentCommand): StoryComment {
        val storyId = StoryId(command.storyId)
        val userId = UserId(command.userId)

        // 스토리 존재 확인
        storyRepository.findById(storyId)
            ?: throw StoryNotFoundException(command.storyId)

        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        // 댓글 생성
        val comment = StoryComment.create(
            storyId = storyId,
            userId = userId,
            userName = user.name,
            userAvatarUrl = user.avatarUrl,
            content = command.content
        )

        return storyCommentRepository.save(comment)
    }

    override fun getStoryComments(storyId: StoryId): List<StoryComment> {
        return storyCommentRepository.findByStoryId(storyId)
    }

    override fun updateStory(command: UpdateStoryCommand): Story {
        val storyId = StoryId(command.storyId)
        val userId = UserId(command.userId)

        // 스토리 조회
        val story = storyRepository.findById(storyId)
            ?: throw StoryNotFoundException(command.storyId)

        // 작성자 권한 확인
        if (story.userId != userId) {
            throw BusinessRuleViolationException("Only story author can update the story")
        }

        // 스토리 수정 (새로운 인스턴스 생성)
        val updatedStory = story.copy(
            storyText = StoryText(command.storyText),
            userTags = command.userTags.map { HashTag(it) }.toMutableList(),
            isPublic = command.isPublic
        )

        val savedStory = storyRepository.save(updatedStory)

        // 해시태그가 변경된 경우 통계 업데이트
        val allTags = (savedStory.userTags + savedStory.autoTags).distinct()
        storyHashtagCoordinator.updateHashtagStatsForStory(allTags)

        return savedStory
    }

    override fun deleteStory(command: DeleteStoryCommand): Boolean {
        val storyId = StoryId(command.storyId)
        storyRepository.findById(storyId)
            ?: return false

        storyRepository.deleteById(storyId)
        return true
    }

    override fun searchStories(query: SearchStoriesQuery): List<Story> {
        return if (query.keyword?.isNotBlank() == true) {
            storyRepository.searchByContent(query.keyword)
        } else {
            emptyList()
        }
    }

    override fun getTrendingHashTags(limit: Int): List<HashTag> {
        // TODO: 실제 구현 필요 - 트렌딩 해시태그 조회
        return emptyList()
    }

    override fun createFreeStory(command: CreateStoryCommand): Story {
        val userId = UserId(command.userId)

        // 사용자 조회
        userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId.toString())

        // 자유 스토리 생성 (미션 없이)
        val story = Story.createFreeStory(
            userId = userId,
            storyText = command.storyText,
            images = command.images,
            location = command.location,
            userTags = command.userTags,
            isPublic = command.isPublic
        )

        // 스토리 저장
        val savedStory = storyRepository.save(story)

        // 해시태그 통계 업데이트
        val allTags = (savedStory.userTags + savedStory.autoTags).distinct()
        storyHashtagCoordinator.updateHashtagStatsForStory(allTags)

        return savedStory
    }

    override fun getStoryFeedByTypeWithCursor(
        storyType: StoryType,
        cursor: Long?,
        size: Int,
        isNext: Boolean,
        userId: UserId?
    ): List<StoryFeedItem> {
        val stories = storyRepository.findPublicStoriesByTypeWithCursor(storyType, cursor, size, isNext)
        return buildStoryFeedItemsForType(stories, storyType, userId)
    }

    private fun buildStoryFeedItemsForType(
        stories: List<Story>,
        storyType: StoryType,
        userId: UserId?
    ): List<StoryFeedItem> {
        return stories.mapNotNull { story ->
            try {
                val user = userRepository.findById(story.userId) ?: return@mapNotNull null

                val isLiked = userId?.let {
                    storyRepository.isLikedByUser(story.id, it)
                } ?: false

                StoryFeedItem(
                    storyId = story.id,
                    user = StoryUser(
                        userId = user.id,
                        name = user.name,
                        avatarUrl = user.avatarUrl,
                        level = user.level,
                        levelTitle = user.levelTitle
                    ),
                    mission = if (storyType == StoryType.MISSION_PROOF) {
                        StoryMission(
                            missionId = story.missionId,
                            title = story.missionTitle,
                            category = story.missionCategory
                        )
                    } else {
                        null
                    },
                    content = StoryContent(
                        storyText = story.storyText,
                        images = story.images,
                        tags = story.userTags
                    ),
                    interactions = StoryInteractions(
                        likes = story.likes,
                        comments = story.comments,
                        isLikedByCurrentUser = isLiked
                    ),
                    timeAgo = story.getTimeAgo(),
                    location = story.location
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun searchStoriesByTypeAndText(storyType: StoryType, query: String, limit: Int): List<StoryFeedItem> {
        val stories = storyRepository.searchStoriesByTypeAndText(storyType, query, limit)
        return buildStoryFeedItemsForType(stories, storyType, null)
    }

    override fun searchStoriesByTypeAndHashtag(storyType: StoryType, hashtag: String, limit: Int): List<StoryFeedItem> {
        val normalizedHashtag = if (hashtag.startsWith("#")) hashtag else "#$hashtag"
        val stories = storyRepository.searchStoriesByTypeAndHashtag(storyType, normalizedHashtag, limit)
        return buildStoryFeedItemsForType(stories, storyType, null)
    }

}