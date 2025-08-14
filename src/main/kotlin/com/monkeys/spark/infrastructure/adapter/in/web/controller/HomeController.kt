package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.*
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/home")
@CrossOrigin(origins = ["http://localhost:3001", "http://localhost:3002", "http://localhost:5173"])
class HomeController(
    private val homePageUseCase: HomePageUseCase,
    private val responseMapper: ResponseMapper
) {

    /**
     * 홈페이지 전체 데이터 조회
     * GET /api/v1/home?userId={userId}
     */
    @GetMapping
    fun getHomePageData(@RequestParam userId: String): ResponseEntity<ApiResponse<HomePageResponse>> {
        val homePageData = homePageUseCase.getHomePageData(UserId(userId))
        
        val response = HomePageResponse(
            userSummary = responseMapper.toUserSummaryResponse(homePageData.userSummary),
            todaysMissions = homePageData.todaysMissions.map { responseMapper.toMissionResponse(it) },
            recentStories = homePageData.recentStories.map { responseMapper.toStoryResponse(it, userId) }
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 사용자 요약 정보 조회
     * GET /api/v1/home/user-summary?userId={userId}
     */
    @GetMapping("/user-summary")
    fun getUserSummary(@RequestParam userId: String): ResponseEntity<ApiResponse<UserSummaryResponse>> {
        val userSummary = homePageUseCase.getUserSummary(UserId(userId))
        val response = responseMapper.toUserSummaryResponse(userSummary)

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 오늘의 추천 미션 조회
     * GET /api/v1/home/todays-missions?userId={userId}
     */
    @GetMapping("/todays-missions")
    fun getTodaysMissions(@RequestParam userId: String): ResponseEntity<ApiResponse<List<MissionResponse>>> {
        val missions = homePageUseCase.getTodaysRecommendedMissions(UserId(userId))
        val response = missions.map { responseMapper.toMissionResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 최근 스토리 조회 (홈페이지용)
     * GET /api/v1/home/recent-stories?limit={limit}
     */
    @GetMapping("/recent-stories")
    fun getRecentStories(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(required = false) userId: String?
    ): ResponseEntity<ApiResponse<List<StoryResponse>>> {
        val stories = homePageUseCase.getRecentStoriesForHome(limit)
        val response = stories.map { responseMapper.toStoryResponse(it, userId) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }
}