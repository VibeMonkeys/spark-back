package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.*
import com.monkeys.spark.application.port.`in`.command.*
import com.monkeys.spark.application.port.`in`.query.*
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.*
import com.monkeys.spark.domain.vo.common.MissionId
import com.monkeys.spark.domain.vo.common.UserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/missions")
@CrossOrigin(origins = ["http://localhost:3001", "http://localhost:3002", "http://localhost:5173"])
class MissionController(
    private val missionUseCase: MissionUseCase,
    private val responseMapper: ResponseMapper,
    private val missionRepository: com.monkeys.spark.application.port.out.MissionRepository,
    private val userApplicationService: com.monkeys.spark.application.service.UserApplicationService
) {

    /**
     * 오늘의 미션 조회 (3개)
     * GET /api/v1/missions/today?userId={userId}
     */
    @GetMapping("/today")
    fun getTodaysMissions(@RequestParam userId: String): ResponseEntity<ApiResponse<List<MissionResponse>>> {
        val missions = missionUseCase.getTodaysMissions(UserId(userId))
        val response = missions.map { responseMapper.toMissionResponse(it) }
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 미션 상세 조회
     * GET /api/v1/missions/{missionId}
     */
    @GetMapping("/{missionId}")
    fun getMissionDetail(@PathVariable missionId: String): ResponseEntity<ApiResponse<MissionDetailResponse>> {
        val mission = missionUseCase.getMissionDetail(MissionId(missionId))
            ?: return ResponseEntity.ok(ApiResponse.error("Mission not found", "MISSION_NOT_FOUND"))

        val similarMissions = missionUseCase.getSimilarMissions(MissionId(missionId), 3)
        val response = responseMapper.toMissionDetailResponse(mission, similarMissions)

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
        try {
            // 진행 중인 미션이 있는지 확인
            val ongoingMissions = missionUseCase.getOngoingMissions(UserId(userId))
            if (ongoingMissions.isNotEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("이미 진행 중인 미션이 있습니다.", "MISSION_IN_PROGRESS")
                )
            }

            // 오늘 시작한 미션 개수 확인 (일일 제한)
            val todayStartedCount = missionRepository.countTodayStartedMissions(UserId(userId))
            if (todayStartedCount >= 3) { // 하루 최대 3개 미션 제한
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("오늘 시작할 수 있는 미션 수를 초과했습니다.", "DAILY_LIMIT_EXCEEDED")
                )
            }

            val templateMission = missionRepository.findById(MissionId(missionId))
                ?: return ResponseEntity.notFound().build()

            // 템플릿 미션인 경우 사용자 전용 미션 생성
            val mission = if (templateMission.isTemplate) {
                val userMission = templateMission.copy(
                    id = MissionId.generate(),
                    userId = UserId(userId),
                    isTemplate = false,
                    assignedAt = java.time.LocalDateTime.now(),
                    expiresAt = java.time.LocalDateTime.now().plusDays(1)
                )
                val savedMission = missionRepository.save(userMission)
                savedMission.start()
                missionRepository.save(savedMission)
            } else {
                val command = StartMissionCommand(missionId, userId)
                missionUseCase.startMission(command)
            }

            val response = responseMapper.toMissionResponse(mission)
            return ResponseEntity.ok(ApiResponse.success(response, "미션을 시작했습니다."))
        } catch (e: Exception) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("알 수 없는 오류가 발생했습니다.", "UNKNOWN_ERROR")
            )
        }
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
        val command = CompleteMissionCommand(missionId, userId)
        val mission = missionUseCase.completeMission(command)
        
        // 업데이트된 사용자 정보 조회
        val user = userApplicationService.getUser(UserId(userId))
            ?: throw IllegalArgumentException("User not found: $userId")
        
        // 획득한 포인트는 미션의 기본 포인트 (실제로는 더 복잡한 계산이 필요할 수 있음)
        val pointsEarned = mission.rewardPoints.value
        
        val response = responseMapper.toMissionCompletionResponse(mission, user, pointsEarned)

        return ResponseEntity.ok(ApiResponse.success(response, "미션을 완료했습니다! ${pointsEarned}P를 획득했습니다."))
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
        val mission = missionUseCase.getMissionDetail(MissionId(missionId))
            ?: return ResponseEntity.ok(ApiResponse.error("Mission not found", "MISSION_NOT_FOUND"))

        // 미션이 해당 사용자의 것인지 확인
        if (mission.userId.value != userId) {
            return ResponseEntity.ok(ApiResponse.error("Mission does not belong to user", "INVALID_USER"))
        }

        // 진행 중인 미션만 포기 가능
        if (mission.status != com.monkeys.spark.domain.vo.mission.MissionStatus.IN_PROGRESS) {
            return ResponseEntity.ok(ApiResponse.error("Only in-progress missions can be abandoned", "INVALID_STATUS"))
        }

        val abandonedMission = mission.expire()
        val savedMission = missionRepository.save(abandonedMission)
        val response = responseMapper.toMissionResponse(savedMission)

        return ResponseEntity.ok(ApiResponse.success(response, "미션이 포기되었습니다."))
    }
}