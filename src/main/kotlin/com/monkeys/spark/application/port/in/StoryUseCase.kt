package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.model.StoryComment
import com.monkeys.spark.application.dto.StoryFeedItem
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.domain.vo.story.StoryType

/**
 * 스토리 관련 UseCase 인터페이스
 */
interface StoryUseCase {
    
    /**
     * 미션 인증 스토리 생성
     */
    fun createStory(command: CreateStoryCommand): Story
    
    /**
     * 스토리 조회
     */
    fun getStory(storyId: StoryId): Story?
    
    /**
     * 스토리 피드 조회 (최신순/인기순)
     */
    fun getStoryFeed(query: StoryFeedQuery): List<StoryFeedItem>
    
    /**
     * 커서 기반 스토리 피드 조회
     */
    fun getStoryFeedWithCursor(userId: String?, cursor: Long?, size: Int, isNext: Boolean): List<StoryFeedItem>
    
    /**
     * 사용자의 스토리 조회
     */
    fun getUserStories(userId: UserId, page: Int, size: Int): List<Story>
    
    /**
     * 커서 기반 사용자 스토리 조회
     */
    fun getUserStoriesWithCursor(userId: UserId, cursor: Long?, size: Int, isNext: Boolean): List<Story>
    
    /**
     * 스토리 좋아요
     */
    fun likeStory(command: LikeStoryCommand): Story
    
    /**
     * 스토리 좋아요 취소
     */
    fun unlikeStory(command: UnlikeStoryCommand): Story
    
    /**
     * 스토리 댓글 추가
     */
    fun addComment(command: AddCommentCommand): StoryComment
    
    /**
     * 스토리 댓글 조회
     */
    fun getStoryComments(storyId: StoryId): List<StoryComment>
    
    /**
     * 스토리 수정 (텍스트만)
     */
    fun updateStory(command: UpdateStoryCommand): Story
    
    /**
     * 스토리 삭제
     */
    fun deleteStory(command: DeleteStoryCommand): Boolean
    
    /**
     * 스토리 검색 (해시태그, 텍스트 검색)
     */
    fun searchStories(query: SearchStoriesQuery): List<Story>
    
    /**
     * 트렌딩 해시태그 조회
     */
    fun getTrendingHashTags(limit: Int): List<HashTag>
    
    /**
     * 자유 스토리 생성
     */
    fun createFreeStory(command: CreateStoryCommand): Story
    
    /**
     * StoryType별 스토리 피드 조회 (커서 기반)
     */
    fun getStoryFeedByTypeWithCursor(storyType: StoryType, cursor: Long?, size: Int, isNext: Boolean, userId: UserId?): List<StoryFeedItem>
    
    /**
     * 스토리 타입별 텍스트 검색
     */
    fun searchStoriesByTypeAndText(storyType: StoryType, query: String, limit: Int): List<StoryFeedItem>
    
    /**
     * 스토리 타입별 해시태그 검색
     */
    fun searchStoriesByTypeAndHashtag(storyType: StoryType, hashtag: String, limit: Int): List<StoryFeedItem>
}