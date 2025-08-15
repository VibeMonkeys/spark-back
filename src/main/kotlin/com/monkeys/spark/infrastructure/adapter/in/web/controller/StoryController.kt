package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.*
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import com.monkeys.spark.domain.vo.common.StoryId
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stories")
class StoryController(
    private val storyUseCase: StoryUseCase,
    private val responseMapper: ResponseMapper
) {

    /**
     * 미션 인증 스토리 생성
     * POST /api/v1/stories
     */
    @PostMapping
    fun createStory(
        @RequestBody request: MissionVerificationRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<MissionVerificationResponse>> {
        val authenticatedUserId = authentication.name // JWT에서 추출된 실제 사용자 ID
        val command = CreateStoryCommand(
            userId = authenticatedUserId,
            missionId = request.missionId,
            storyText = request.story,
            images = request.images,
            location = request.location,
            isPublic = request.isPublic,
            userTags = request.userTags
        )

        val story = storyUseCase.createStory(command)
        
        // TODO: 실제 포인트 획득 및 스트릭 정보 계산
        val response = MissionVerificationResponse(
            storyId = story.id.value,
            pointsEarned = 20, // 임시 값
            streakCount = 7, // 임시 값
            levelUp = false,
            newLevel = null
        )

        return ResponseEntity.ok(ApiResponse.success(response, "미션 인증이 완료되었습니다."))
    }

    /**
     * 스토리 피드 조회
     * GET /api/v1/stories/feed?sortBy={sortBy}&page={page}&size={size}
     */
    @GetMapping("/feed")
    fun getStoryFeed(
        @RequestParam(defaultValue = "latest") sortBy: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) userId: String?
    ): ResponseEntity<ApiResponse<PagedResponse<StoryResponse>>> {
        val query = StoryFeedQuery(userId, sortBy, page, size, category)
        val feedItems = storyUseCase.getStoryFeed(query)
        val storyResponses = feedItems.map { responseMapper.toStoryResponse(it, userId) }

        // 임시 페이징 정보 생성
        val pageInfo = PageInfo(
            currentPage = page,
            pageSize = size,
            totalElements = feedItems.size.toLong(),
            totalPages = (feedItems.size / size) + 1,
            hasNext = feedItems.size >= size,
            hasPrevious = page > 0
        )

        val pagedResponse = PagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 스토리 상세 조회
     * GET /api/v1/stories/{storyId}
     */
    @GetMapping("/{storyId}")
    fun getStory(
        @PathVariable storyId: String,
        @RequestParam(required = false) userId: String?
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val story = storyUseCase.getStory(StoryId(storyId))
            ?: return ResponseEntity.ok(ApiResponse.error("Story not found", "STORY_NOT_FOUND"))

        val response = responseMapper.toStoryResponse(story, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 사용자의 스토리 조회
     * GET /api/v1/stories/user/{userId}?page={page}&size={size}
     */
    @GetMapping("/user/{targetUserId}")
    fun getUserStories(
        @PathVariable targetUserId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) currentUserId: String?
    ): ResponseEntity<ApiResponse<PagedResponse<StoryResponse>>> {
        val stories = storyUseCase.getUserStories(UserId(targetUserId), page, size)
        val storyResponses = stories.map { responseMapper.toStoryResponse(it, currentUserId) }

        // 임시 페이징 정보
        val pageInfo = PageInfo(
            currentPage = page,
            pageSize = size,
            totalElements = stories.size.toLong(),
            totalPages = (stories.size / size) + 1,
            hasNext = stories.size >= size,
            hasPrevious = page > 0
        )

        val pagedResponse = PagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 스토리 좋아요
     * POST /api/v1/stories/{storyId}/like
     */
    @PostMapping("/{storyId}/like")
    fun likeStory(
        @PathVariable storyId: String,
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val command = LikeStoryCommand(storyId, userId)
        val story = storyUseCase.likeStory(command)
        val response = responseMapper.toStoryResponse(story, userId)

        return ResponseEntity.ok(ApiResponse.success(response, "좋아요를 눌렀습니다."))
    }

    /**
     * 스토리 좋아요 취소
     * DELETE /api/v1/stories/{storyId}/like
     */
    @DeleteMapping("/{storyId}/like")
    fun unlikeStory(
        @PathVariable storyId: String,
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val command = UnlikeStoryCommand(storyId, userId)
        val story = storyUseCase.unlikeStory(command)
        val response = responseMapper.toStoryResponse(story, userId)

        return ResponseEntity.ok(ApiResponse.success(response, "좋아요를 취소했습니다."))
    }

    /**
     * 스토리 댓글 조회
     * GET /api/v1/stories/{storyId}/comments
     */
    @GetMapping("/{storyId}/comments")
    fun getStoryComments(@PathVariable storyId: String): ResponseEntity<ApiResponse<List<StoryCommentResponse>>> {
        val comments = storyUseCase.getStoryComments(StoryId(storyId))
        val response = comments.map { responseMapper.toStoryCommentResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 스토리 댓글 추가
     * POST /api/v1/stories/{storyId}/comments
     */
    @PostMapping("/{storyId}/comments")
    fun addComment(
        @PathVariable storyId: String,
        @RequestParam userId: String,
        @RequestBody request: AddCommentRequest
    ): ResponseEntity<ApiResponse<StoryCommentResponse>> {
        val command = AddCommentCommand(storyId, userId, request.content)
        val comment = storyUseCase.addComment(command)
        val response = responseMapper.toStoryCommentResponse(comment)

        return ResponseEntity.ok(ApiResponse.success(response, "댓글이 추가되었습니다."))
    }

    /**
     * 스토리 수정
     * PUT /api/v1/stories/{storyId}
     */
    @PutMapping("/{storyId}")
    fun updateStory(
        @PathVariable storyId: String,
        @RequestParam userId: String,
        @RequestBody request: UpdateStoryRequest
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val command = UpdateStoryCommand(
            storyId = storyId,
            userId = userId,
            storyText = request.storyText,
            userTags = request.userTags,
            isPublic = request.isPublic
        )
        val story = storyUseCase.updateStory(command)
        val response = responseMapper.toStoryResponse(story, userId)

        return ResponseEntity.ok(ApiResponse.success(response, "스토리가 수정되었습니다."))
    }

    /**
     * 스토리 삭제
     * DELETE /api/v1/stories/{storyId}
     */
    @DeleteMapping("/{storyId}")
    fun deleteStory(
        @PathVariable storyId: String,
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<String>> {
        val command = DeleteStoryCommand(storyId, userId)
        val success = storyUseCase.deleteStory(command)

        return if (success) {
            ResponseEntity.ok(ApiResponse.success("스토리가 삭제되었습니다."))
        } else {
            ResponseEntity.ok(ApiResponse.error("스토리 삭제에 실패했습니다.", "DELETE_FAILED"))
        }
    }

    /**
     * 스토리 검색
     * GET /api/v1/stories/search?keyword={keyword}&category={category}
     */
    @GetMapping("/search")
    fun searchStories(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) hashTag: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) location: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) userId: String?
    ): ResponseEntity<ApiResponse<PagedResponse<StoryResponse>>> {
        val query = SearchStoriesQuery(keyword, hashTag, category, location, page, size)
        val stories = storyUseCase.searchStories(query)
        val storyResponses = stories.map { responseMapper.toStoryResponse(it, userId) }

        // 임시 페이징 정보
        val pageInfo = PageInfo(
            currentPage = page,
            pageSize = size,
            totalElements = stories.size.toLong(),
            totalPages = (stories.size / size) + 1,
            hasNext = stories.size >= size,
            hasPrevious = page > 0
        )

        val pagedResponse = PagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }
}


