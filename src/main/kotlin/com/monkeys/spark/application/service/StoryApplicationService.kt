package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.StoryUseCase
import com.monkeys.spark.application.port.`in`.command.CreateStoryCommand
import com.monkeys.spark.application.port.`in`.command.LikeStoryCommand
import com.monkeys.spark.application.port.`in`.command.UnlikeStoryCommand
import com.monkeys.spark.application.port.`in`.command.AddCommentCommand
import com.monkeys.spark.application.port.`in`.command.UpdateStoryCommand
import com.monkeys.spark.application.port.`in`.command.DeleteStoryCommand
import com.monkeys.spark.application.port.`in`.query.StoryFeedQuery
import com.monkeys.spark.application.port.`in`.query.SearchStoriesQuery
import com.monkeys.spark.application.port.out.StoryRepository
import com.monkeys.spark.application.port.out.StoryCommentRepository
import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.domain.model.*
import com.monkeys.spark.application.dto.*
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.story.*
import com.monkeys.spark.domain.service.StoryMissionDomainService
import com.monkeys.spark.domain.exception.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StoryApplicationService(
    private val storyRepository: StoryRepository,
    private val storyCommentRepository: StoryCommentRepository,
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository
) : StoryUseCase {
    
    private val storyMissionDomainService = StoryMissionDomainService()

    override fun createStory(command: CreateStoryCommand): Story {
        val userId = UserId(command.userId)
        val missionId = MissionId(command.missionId)
        
        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId)
        
        // 미션 조회 및 검증
        val mission = missionRepository.findById(missionId)
            ?: throw MissionNotFoundException(command.missionId)
        
        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId != userId) {
            throw BusinessRuleViolationException("Mission does not belong to user: ${command.userId}")
        }
        
        // 스토리 생성
        val story = Story.create(
            userId = userId,
            missionId = missionId,
            missionTitle = mission.title,
            missionCategory = mission.category,
            storyText = command.storyText,
            images = command.images,
            location = command.location,
            isPublic = command.isPublic
        )
        
        // 스토리 저장
        val savedStory = storyRepository.save(story)
        
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
        
        // Category 필터링
        val filteredStories = if (query.category != null) {
            storyRepository.findByMissionCategory(com.monkeys.spark.domain.vo.mission.MissionCategory.valueOf(query.category))
        } else {
            stories
        }
        
        // StoryFeedItem으로 변환
        val feedItems = filteredStories.mapNotNull { story ->
            try {
                val user = userRepository.findById(story.userId) ?: return@mapNotNull null
                
                val isLiked = query.userId?.let { 
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
        
        return feedItems
    }
    
    override fun getUserStories(userId: UserId, page: Int, size: Int): List<Story> {
        return storyRepository.findByUserId(userId)
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
        val story = storyRepository.findById(storyId)
            ?: throw StoryNotFoundException(command.storyId)
        
        // 사용자 조회
        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(command.userId)
        
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
            storyText = command.storyText?.let { StoryText(it) } ?: story.storyText,
            userTags = command.userTags?.map { HashTag(it) }?.toMutableList() ?: story.userTags,
            isPublic = command.isPublic ?: story.isPublic
        )
        
        return storyRepository.save(updatedStory)
    }
    
    override fun deleteStory(command: DeleteStoryCommand): Boolean {
        val storyId = StoryId(command.storyId)
        val story = storyRepository.findById(storyId)
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
}