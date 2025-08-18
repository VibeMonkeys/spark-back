package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.StoryUseCase
import com.monkeys.spark.application.port.`in`.command.CreateStoryCommand
import com.monkeys.spark.application.port.`in`.command.LikeStoryCommand
import com.monkeys.spark.application.port.`in`.command.UnlikeStoryCommand
import com.monkeys.spark.application.port.`in`.command.AddCommentCommand
import com.monkeys.spark.application.port.`in`.command.UpdateStoryCommand
import com.monkeys.spark.application.port.`in`.command.DeleteStoryCommand
import com.monkeys.spark.application.port.`in`.query.SearchStoriesQuery
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.CursorPagedResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.CursorPageInfo
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.CursorDirection
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import com.monkeys.spark.domain.vo.common.StoryId
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.story.StoryType
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
            userId = authenticatedUserId.toLong(),
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
     * 스토리 피드 조회 (커서 기반 페이지네이션)
     * GET /api/v1/stories/feed?cursor={cursor}&size={size}&direction={direction}
     */
    @GetMapping("/feed")
    fun getStoryFeed(
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "NEXT") direction: String,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<ApiResponse<CursorPagedResponse<StoryResponse>>> {
        val isNext = direction.uppercase() == "NEXT"
        val feedItems = storyUseCase.getStoryFeedWithCursor(userId?.toString(), cursor, size, isNext)
        val storyResponses = feedItems.map { responseMapper.toStoryResponse(it, userId?.toString()) }

        // 커서 페이징 정보 생성
        val nextCursor = if (storyResponses.isNotEmpty() && storyResponses.size >= size) {
            storyResponses.last().id.toString()
        } else null
        
        val previousCursor = if (storyResponses.isNotEmpty()) {
            storyResponses.first().id.toString()
        } else null

        val pageInfo = CursorPageInfo(
            hasNext = storyResponses.size >= size,
            hasPrevious = cursor != null,
            nextCursor = nextCursor,
            previousCursor = if (cursor != null) previousCursor else null,
            pageSize = size
        )

        val pagedResponse = CursorPagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 스토리 상세 조회
     * GET /api/v1/stories/{storyId}
     */
    @GetMapping("/{storyId}")
    fun getStory(
        @PathVariable storyId: Long,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val story = storyUseCase.getStory(StoryId(storyId))
            ?: return ResponseEntity.ok(ApiResponse.error("Story not found", "STORY_NOT_FOUND"))

        val response = responseMapper.toStoryResponse(story, userId?.toString())
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 사용자의 스토리 조회 (커서 기반)
     * GET /api/v1/stories/user/{userId}?cursor={cursor}&size={size}
     */
    @GetMapping("/user/{targetUserId}")
    fun getUserStories(
        @PathVariable targetUserId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "NEXT") direction: String,
        @RequestParam(required = false) currentUserId: Long?
    ): ResponseEntity<ApiResponse<CursorPagedResponse<StoryResponse>>> {
        val isNext = direction.uppercase() == "NEXT"
        val stories = storyUseCase.getUserStoriesWithCursor(UserId(targetUserId), cursor, size, isNext)
        val storyResponses = stories.map { responseMapper.toStoryResponse(it, currentUserId?.toString()) }

        // 커서 페이징 정보
        val nextCursor = if (storyResponses.isNotEmpty() && storyResponses.size >= size) {
            storyResponses.last().id.toString()
        } else null
        
        val previousCursor = if (storyResponses.isNotEmpty()) {
            storyResponses.first().id.toString()
        } else null

        val pageInfo = CursorPageInfo(
            hasNext = storyResponses.size >= size,
            hasPrevious = cursor != null,
            nextCursor = nextCursor,
            previousCursor = if (cursor != null) previousCursor else null,
            pageSize = size
        )

        val pagedResponse = CursorPagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 스토리 좋아요
     * POST /api/v1/stories/{storyId}/like
     */
    @PostMapping("/{storyId}/like")
    fun likeStory(
        @PathVariable storyId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val command = LikeStoryCommand(storyId, userId)
        val story = storyUseCase.likeStory(command)
        val response = responseMapper.toStoryResponse(story, userId.toString())

        return ResponseEntity.ok(ApiResponse.success(response, "좋아요를 눌렀습니다."))
    }

    /**
     * 스토리 좋아요 취소
     * DELETE /api/v1/stories/{storyId}/like
     */
    @DeleteMapping("/{storyId}/like")
    fun unlikeStory(
        @PathVariable storyId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val command = UnlikeStoryCommand(storyId, userId)
        val story = storyUseCase.unlikeStory(command)
        val response = responseMapper.toStoryResponse(story, userId.toString())

        return ResponseEntity.ok(ApiResponse.success(response, "좋아요를 취소했습니다."))
    }

    /**
     * 스토리 댓글 조회
     * GET /api/v1/stories/{storyId}/comments
     */
    @GetMapping("/{storyId}/comments")
    fun getStoryComments(@PathVariable storyId: Long): ResponseEntity<ApiResponse<List<StoryCommentResponse>>> {
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
        @PathVariable storyId: Long,
        @RequestParam userId: Long,
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
        @PathVariable storyId: Long,
        @RequestParam userId: Long,
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
        val response = responseMapper.toStoryResponse(story, userId.toString())

        return ResponseEntity.ok(ApiResponse.success(response, "스토리가 수정되었습니다."))
    }

    /**
     * 스토리 삭제
     * DELETE /api/v1/stories/{storyId}
     */
    @DeleteMapping("/{storyId}")
    fun deleteStory(
        @PathVariable storyId: Long,
        @RequestParam userId: Long
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
     * StoryType별 스토리 피드 조회 (커서 기반 페이지네이션)
     * GET /api/v1/stories/feed/{storyType}?cursor={cursor}&size={size}&direction={direction}
     */
    @GetMapping("/feed/{storyType}")
    fun getStoryFeedByType(
        @PathVariable storyType: String,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "NEXT") direction: String,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<ApiResponse<CursorPagedResponse<StoryResponse>>> {
        val storyTypeEnum = StoryType.fromString(storyType)

        val isNext = direction.uppercase() == "NEXT"
        val feedItems = storyUseCase.getStoryFeedByTypeWithCursor(storyTypeEnum, cursor, size, isNext, userId?.let { UserId(it) })
        val storyResponses = feedItems.map { responseMapper.toStoryResponse(it, userId?.toString()) }

        // 커서 페이징 정보 생성
        val nextCursor = if (storyResponses.isNotEmpty() && storyResponses.size >= size) {
            storyResponses.last().id.toString()
        } else null
        
        val previousCursor = if (storyResponses.isNotEmpty()) {
            storyResponses.first().id.toString()
        } else null

        val pageInfo = CursorPageInfo(
            hasNext = storyResponses.size >= size,
            hasPrevious = cursor != null,
            nextCursor = nextCursor,
            previousCursor = if (cursor != null) previousCursor else null,
            pageSize = size
        )

        val pagedResponse = CursorPagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 자유 스토리 생성
     * POST /api/v1/stories/free
     */
    @PostMapping("/free")
    fun createFreeStory(
        @RequestBody request: CreateFreeStoryRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<StoryResponse>> {
        val authenticatedUserId = authentication.name // JWT에서 추출된 실제 사용자 ID
        val command = CreateStoryCommand(
            userId = authenticatedUserId.toLong(),
            missionId = 0L, // 자유 스토리는 미션 ID가 0 (사용되지 않음)
            storyText = request.storyText,
            images = request.images,
            location = request.location,
            isPublic = request.isPublic,
            userTags = request.userTags
        )

        val story = storyUseCase.createFreeStory(command)
        val response = responseMapper.toStoryResponse(story, authenticatedUserId)

        return ResponseEntity.ok(ApiResponse.success(response, "자유 스토리가 생성되었습니다."))
    }

    /**
     * 스토리 검색 (커서 기반 페이지네이션으로 변경)
     * GET /api/v1/stories/search?keyword={keyword}&category={category}&cursor={cursor}&size={size}
     */
    @GetMapping("/search")
    fun searchStories(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) hashTag: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) location: String?,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "NEXT") direction: String,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<ApiResponse<CursorPagedResponse<StoryResponse>>> {
        val isNext = direction.uppercase() == "NEXT"
        val query = SearchStoriesQuery(keyword, hashTag, category, location, cursor, size, isNext)
        val stories = storyUseCase.searchStoriesWithCursor(query)
        val storyResponses = stories.map { responseMapper.toStoryResponse(it, userId?.toString()) }

        // 커서 페이징 정보 생성
        val nextCursor = if (storyResponses.isNotEmpty() && storyResponses.size >= size) {
            storyResponses.last().id.toString()
        } else null
        
        val previousCursor = if (storyResponses.isNotEmpty()) {
            storyResponses.first().id.toString()
        } else null

        val pageInfo = CursorPageInfo(
            hasNext = storyResponses.size >= size,
            hasPrevious = cursor != null,
            nextCursor = nextCursor,
            previousCursor = if (cursor != null) previousCursor else null,
            pageSize = size
        )

        val pagedResponse = CursorPagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 스토리 타입별 텍스트 검색
     * GET /api/v1/stories/search/{storyType}?query={query}&limit={limit}
     */
    @GetMapping("/search/{storyType}")
    fun searchStoriesByType(
        @PathVariable storyType: String,
        @RequestParam query: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<ApiResponse<CursorPagedResponse<StoryResponse>>> {
        val storyTypeEnum = StoryType.fromString(storyType)

        val feedItems = storyUseCase.searchStoriesByTypeAndText(storyTypeEnum, query, limit)
        val storyResponses = feedItems.map { responseMapper.toStoryResponse(it, null) }

        // 검색 결과는 커서 페이징 정보를 기본값으로 설정
        val pageInfo = CursorPageInfo(
            hasNext = false,
            hasPrevious = false,
            nextCursor = null,
            previousCursor = null,
            pageSize = limit
        )

        val pagedResponse = CursorPagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 스토리 타입별 해시태그 검색
     * GET /api/v1/stories/search/{storyType}/hashtag?hashtag={hashtag}&limit={limit}
     */
    @GetMapping("/search/{storyType}/hashtag")
    fun searchStoriesByHashtag(
        @PathVariable storyType: String,
        @RequestParam hashtag: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<ApiResponse<CursorPagedResponse<StoryResponse>>> {
        val storyTypeEnum = StoryType.fromString(storyType)

        val feedItems = storyUseCase.searchStoriesByTypeAndHashtag(storyTypeEnum, hashtag, limit)
        val storyResponses = feedItems.map { responseMapper.toStoryResponse(it, null) }

        // 검색 결과는 커서 페이징 정보를 기본값으로 설정
        val pageInfo = CursorPageInfo(
            hasNext = false,
            hasPrevious = false,
            nextCursor = null,
            previousCursor = null,
            pageSize = limit
        )

        val pagedResponse = CursorPagedResponse(storyResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }
}


