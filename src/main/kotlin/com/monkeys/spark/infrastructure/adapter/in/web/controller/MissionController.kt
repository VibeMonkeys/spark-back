package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.MissionUseCase
import com.monkeys.spark.application.port.`in`.StoryUseCase
import com.monkeys.spark.application.port.`in`.UserStatsUseCase
import com.monkeys.spark.application.port.`in`.command.StartMissionCommand
import com.monkeys.spark.application.port.`in`.command.UpdateProgressCommand
import com.monkeys.spark.application.port.`in`.command.CompleteMissionCommand
import com.monkeys.spark.application.port.`in`.command.CreateStoryCommand
import com.monkeys.spark.application.port.`in`.command.AbandonMissionCommand
import com.monkeys.spark.application.port.`in`.query.CompletedMissionsQuery
import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.application.service.UserApplicationService
import com.monkeys.spark.domain.model.UserStats.Companion.MISSION_COMPLETION_ALLOCATABLE_POINTS
import com.monkeys.spark.domain.model.UserStats.Companion.MISSION_COMPLETION_STAT_POINTS
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.PageInfo
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.PagedResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.MissionVerificationRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/missions")
class MissionController(
    private val missionUseCase: MissionUseCase,
    private val storyUseCase: StoryUseCase,
    private val userStatsUseCase: UserStatsUseCase,
    private val responseMapper: ResponseMapper,
    private val missionRepository: MissionRepository,
    private val userApplicationService: UserApplicationService
) {

    /**
     * 오늘의 미션 조회 (5개) - 제한 정보 포함
     * GET /api/v1/missions/today?userId={userId}
     */
    @GetMapping("/today")
    fun getTodaysMissions(@RequestParam userId: String): ResponseEntity<ApiResponse<TodaysMissionsResponse>> {
        val userIdVO = UserId(userId)
        val missions = missionUseCase.getTodaysMissions(userIdVO)
        val missionResponses = missions.map { responseMapper.toMissionResponse(it) }

        // 일일 제한 정보 조회
        val validation = missionRepository.canStartMission(userIdVO)
        val dailyLimitResponse = DailyMissionLimitResponse(
            maxDailyStarts = validation.dailyLimit.maxDailyStarts,
            currentStarted = validation.dailyLimit.currentStarted.toInt(),
            remainingStarts = validation.dailyLimit.remainingStarts,
            canStart = validation.dailyLimit.canStart,
            resetTime = validation.dailyLimit.resetTime
        )

        val response = TodaysMissionsResponse(
            missions = missionResponses,
            dailyLimit = dailyLimitResponse
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 미션 상세 조회
     * GET /api/v1/missions/{missionId}
     */
    @GetMapping("/{missionId}")
    fun getMissionDetail(@PathVariable missionId: String): ResponseEntity<ApiResponse<MissionDetailResponse>> {
        val mission = missionUseCase.getMissionDetail(MissionId(missionId))
        val similarMissions = missionUseCase.getSimilarMissions(MissionId(missionId), 3)
        val response = responseMapper.toMissionDetailResponse(mission, similarMissions)

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 일일 미션 시작 제한 정보 조회
     * GET /api/v1/missions/daily-limit?userId={userId}
     */
    @GetMapping("/daily-limit")
    fun getDailyMissionLimit(@RequestParam userId: String): ResponseEntity<ApiResponse<DailyMissionLimitResponse>> {
        val userIdVO = UserId(userId)
        val validation = missionRepository.canStartMission(userIdVO)

        val response = DailyMissionLimitResponse(
            maxDailyStarts = validation.dailyLimit.maxDailyStarts,
            currentStarted = validation.dailyLimit.currentStarted.toInt(),
            remainingStarts = validation.dailyLimit.remainingStarts,
            canStart = validation.dailyLimit.canStart,
            resetTime = validation.dailyLimit.resetTime
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 미션 시작
     * POST /api/v1/missions/{missionId}/start
     */
    @PostMapping("/{missionId}/start")
    fun startMission(
        @PathVariable missionId: String,
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<MissionResponse>> {
        val command = StartMissionCommand(missionId, userId)
        val mission = missionUseCase.startMission(command)

        val response = responseMapper.toMissionResponse(mission)
        return ResponseEntity.ok(ApiResponse.success(response, "미션을 시작했습니다."))
    }

    /**
     * 미션 진행도 업데이트
     * PUT /api/v1/missions/{missionId}/progress
     */
    @PutMapping("/{missionId}/progress")
    fun updateMissionProgress(
        @PathVariable missionId: String,
        @RequestParam userId: String,
        @RequestParam progress: Int
    ): ResponseEntity<ApiResponse<MissionResponse>> {
        val command = UpdateProgressCommand(missionId, userId, progress)
        val mission = missionUseCase.updateMissionProgress(command)
        val response = responseMapper.toMissionResponse(mission)

        return ResponseEntity.ok(ApiResponse.success(response, "진행도가 업데이트되었습니다."))
    }

    /**
     * 미션 완료
     * POST /api/v1/missions/{missionId}/complete
     */
    @PostMapping("/{missionId}/complete")
    fun completeMission(
        @PathVariable missionId: String,
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<MissionCompletionResponse>> {
        val userIdVO = UserId(userId)
        val command = CompleteMissionCommand(missionId, userId)
        val mission = missionUseCase.completeMission(command)

        // 업데이트된 사용자 정보 조회
        val user = userApplicationService.getUser(userIdVO)
            ?: throw IllegalArgumentException("User not found: $userId")

        // 획득한 포인트는 미션의 기본 포인트 (실제로는 더 복잡한 계산이 필요할 수 있음)
        val pointsEarned = mission.rewardPoints.value

        // 남은 미션 목록 조회 (완료 후 상태 업데이트를 위해)
        val remainingMissions = missionUseCase.getTodaysMissions(userIdVO)
            .filter { it.status.name == "ASSIGNED" || it.status.name == "IN_PROGRESS" }
            .map { responseMapper.toMissionResponse(it) }

        val response = responseMapper.toMissionCompletionResponse(mission, user, pointsEarned).copy(
            remainingMissions = remainingMissions
        )

        return ResponseEntity.ok(ApiResponse.success(response, "미션을 완료했습니다! ${pointsEarned}P를 획득했습니다."))
    }

    /**
     * 미션 인증 및 완료 (스토리 자동 생성 포함)
     * POST /api/v1/missions/{missionId}/verify
     */
    @PostMapping("/{missionId}/verify")
    fun verifyMission(
        @PathVariable missionId: String,
        @RequestBody request: MissionVerificationRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<MissionVerificationResponse>> {
        val authenticatedUserId = authentication.name

        // 1. 미션 완료 처리
        val completeMissionCommand = CompleteMissionCommand(missionId, authenticatedUserId)
        val completedMission = missionUseCase.completeMission(completeMissionCommand)

        // 2. 스탯 증가 처리
        val updatedStats = userStatsUseCase.increaseMissionStat(
            UserId(authenticatedUserId),
            completedMission.category.name
        )

        // 3. 스토리 생성 (스토리가 있는 경우에만)
        val story = if (request.story.trim().isNotEmpty() || request.images.isNotEmpty()) {
            val createStoryCommand = CreateStoryCommand(
                userId = authenticatedUserId,
                missionId = missionId,
                storyText = request.story.trim().ifEmpty { "미션을 완료했습니다! 🎉" },
                images = request.images,
                location = request.location,
                isPublic = request.isPublic,
                userTags = request.userTags
            )
            storyUseCase.createStory(createStoryCommand)
        } else null

        // 4. 사용자 정보 조회 (포인트 업데이트 반영)
        val user = userApplicationService.getUser(UserId(authenticatedUserId))
            ?: throw IllegalArgumentException("User not found: $authenticatedUserId")

        // 5. 응답 생성
        val response = MissionVerificationResponse(
            storyId = story?.id?.value ?: "",
            pointsEarned = completedMission.rewardPoints.value,
            streakCount = user.currentStreak.value,
            levelUp = false, // TODO: 레벨업 로직 추가
            newLevel = null,
            // 스탯 정보 추가
            statsIncreased = mapOf(
                "category" to completedMission.category.name,
                "pointsGained" to MISSION_COMPLETION_STAT_POINTS,
                "allocatablePointsGained" to MISSION_COMPLETION_ALLOCATABLE_POINTS,
                "totalStats" to updatedStats.totalStats
            )
        )

        return ResponseEntity.ok(ApiResponse.success(response, "미션 인증이 완료되었습니다."))
    }

    /**
     * 진행 중인 미션 조회
     * GET /api/v1/missions/ongoing?userId={userId}
     */
    @GetMapping("/ongoing")
    fun getOngoingMissions(@RequestParam userId: String): ResponseEntity<ApiResponse<List<MissionResponse>>> {
        val missions = missionUseCase.getOngoingMissions(UserId(userId))
        val response = missions.map { responseMapper.toMissionResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 완료된 미션 조회 (페이징)
     * GET /api/v1/missions/completed?userId={userId}&page={page}&size={size}
     */
    @GetMapping("/completed")
    fun getCompletedMissions(
        @RequestParam userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) category: String?
    ): ResponseEntity<ApiResponse<PagedResponse<MissionResponse>>> {
        val query = CompletedMissionsQuery(userId, page, size, category)
        val missions = missionUseCase.getCompletedMissions(query)
        val missionResponses = missions.map { responseMapper.toMissionResponse(it) }

        // 임시로 페이징 정보 생성 (실제로는 Repository에서 카운트 쿼리 필요)
        val pageInfo = PageInfo(
            currentPage = page,
            pageSize = size,
            totalElements = missions.size.toLong(),
            totalPages = (missions.size / size) + 1,
            hasNext = missions.size >= size,
            hasPrevious = page > 0
        )

        val pagedResponse = PagedResponse(missionResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 인기 미션 조회
     * GET /api/v1/missions/popular?limit={limit}
     */
    @GetMapping("/popular")
    fun getPopularMissions(
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<MissionResponse>>> {
        val missions = missionUseCase.getPopularMissions(limit)
        val response = missions.map { responseMapper.toMissionResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 미션 리롤 (새로운 미션으로 교체)
     * POST /api/v1/missions/reroll
     */
    @PostMapping("/reroll")
    fun rerollMissions(@RequestParam userId: String): ResponseEntity<ApiResponse<List<MissionResponse>>> {
        val missions = missionUseCase.rerollMissions(UserId(userId))
        val response = missions.map { responseMapper.toMissionResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response, "새로운 미션들로 교체되었습니다."))
    }

    /**
     * 카테고리별 통계 조회
     * GET /api/v1/missions/category-stats?userId={userId}
     */
    @GetMapping("/category-stats")
    fun getCategoryStatistics(
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<List<CategoryStatResponse>>> {
        val stats = missionUseCase.getCategoryStatistics(UserId(userId))
        val response = stats.map { (category, stat) ->
            responseMapper.toCategoryStatResponse(category, stat)
        }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 일일 미션 생성 (관리자용 또는 시스템 호출)
     * POST /api/v1/missions/generate-daily
     */
    @PostMapping("/generate-daily")
    fun generateDailyMissions(@RequestParam userId: String): ResponseEntity<ApiResponse<List<MissionResponse>>> {
        val missions = missionUseCase.generateDailyMissions(UserId(userId))
        val response = missions.map { responseMapper.toMissionResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response, "일일 미션이 생성되었습니다."))
    }

    /**
     * 미션 포기
     * POST /api/v1/missions/{missionId}/abandon
     */
    @PostMapping("/{missionId}/abandon")
    fun abandonMission(
        @PathVariable missionId: String,
        @RequestParam userId: String
    ): ResponseEntity<ApiResponse<MissionResponse>> {
        val command = AbandonMissionCommand(missionId, userId)
        val mission = missionUseCase.abandonMission(command)
        val response = responseMapper.toMissionResponse(mission)

        return ResponseEntity.ok(ApiResponse.success(response, "미션이 포기되었습니다."))
    }

}