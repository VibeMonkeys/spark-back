package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.StoryId
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.Location
import com.monkeys.spark.domain.vo.mission.MissionCategory
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.domain.vo.story.StoryType
import java.time.LocalDateTime

interface StoryRepository {
    
    /**
     * 스토리 저장 (생성 및 수정)
     */
    fun save(story: Story): Story
    
    /**
     * 스토리 ID로 조회
     */
    fun findById(storyId: StoryId): Story?
    
    /**
     * 사용자의 모든 스토리 조회
     */
    fun findByUserId(userId: UserId): List<Story>
    
    /**
     * 공개 스토리만 조회 (페이징)
     */
    fun findPublicStories(page: Int, size: Int): List<Story>
    
    /**
     * 커서 기반 공개 스토리 조회 (최신순)
     */
    fun findPublicStoriesWithCursor(cursor: Long?, size: Int, isNext: Boolean = true): List<Story>
    
    /**
     * StoryType별 공개 스토리 조회 (커서 기반)
     */
    fun findPublicStoriesByTypeWithCursor(storyType: StoryType, cursor: Long?, size: Int, isNext: Boolean = true): List<Story>
    
    /**
     * StoryType별 공개 스토리 조회 (페이징)
     */
    fun findPublicStoriesByType(storyType: StoryType, page: Int, size: Int): List<Story>
    
    /**
     * 인기 스토리 조회 (좋아요 수 기준)
     */
    fun findPopularStories(limit: Int): List<Story>
    
    /**
     * 최신 스토리 조회
     */
    fun findRecentStories(limit: Int): List<Story>
    
    /**
     * 특정 미션의 스토리들 조회
     */
    fun findByMissionId(missionId: MissionId): List<Story>
    
    /**
     * 카테고리별 스토리 조회
     */
    fun findByMissionCategory(category: MissionCategory): List<Story>
    
    /**
     * 위치별 스토리 조회
     */
    fun findByLocation(location: Location): List<Story>
    
    /**
     * 해시태그로 스토리 검색
     */
    fun findByHashTag(hashTag: HashTag): List<Story>
    
    /**
     * 텍스트 검색으로 스토리 찾기
     */
    fun searchByContent(keyword: String): List<Story>
    
    /**
     * 사용자 피드용 스토리 조회 (팔로우하는 사용자들의 스토리)
     * 현재는 전체 공개 스토리로 대체
     */
    fun findFeedStories(userId: UserId, page: Int, size: Int): List<Story>
    
    /**
     * 커서 기반 피드 스토리 조회
     */
    fun findFeedStoriesWithCursor(userId: UserId?, cursor: Long?, size: Int, isNext: Boolean = true): List<Story>
    
    /**
     * 사용자별 커서 기반 스토리 조회
     */
    fun findUserStoriesWithCursor(userId: UserId, cursor: Long?, size: Int, isNext: Boolean = true): List<Story>
    
    /**
     * 특정 기간 동안의 스토리 조회
     */
    fun findByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Story>
    
    /**
     * 스토리 좋아요
     */
    fun likeStory(storyId: StoryId, userId: UserId): Story?
    
    /**
     * 스토리 좋아요 취소
     */
    fun unlikeStory(storyId: StoryId, userId: UserId): Story?
    
    /**
     * 사용자가 특정 스토리를 좋아요했는지 확인
     */
    fun isLikedByUser(storyId: StoryId, userId: UserId): Boolean
    
    /**
     * 스토리 삭제
     */
    fun deleteById(storyId: StoryId)
    
    /**
     * 사용자의 스토리 전체 삭제
     */
    fun deleteByUserId(userId: UserId)
    
    /**
     * 특정 미션의 스토리 전체 삭제
     */
    fun deleteByMissionId(missionId: MissionId)
    
    /**
     * 스토리 타입별 텍스트 검색
     */
    fun searchStoriesByTypeAndText(storyType: StoryType, query: String, limit: Int): List<Story>
    
    /**
     * 스토리 타입별 해시태그 검색
     */
    fun searchStoriesByTypeAndHashtag(storyType: StoryType, hashtag: String, limit: Int): List<Story>
}