package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.RewardUseCase
import com.monkeys.spark.application.port.`in`.command.ExchangeRewardCommand
import com.monkeys.spark.application.port.`in`.command.UseRewardCommand
import com.monkeys.spark.application.port.`in`.query.AvailableRewardsQuery
import com.monkeys.spark.application.port.`in`.query.RewardStatistics
import com.monkeys.spark.application.port.`in`.query.UserRewardsQuery
import com.monkeys.spark.domain.vo.common.RewardId
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.reward.RewardCategory
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.PageInfo
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.PagedResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.RewardResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.RewardsPageResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.UserPointsResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.UserRewardResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/rewards")
class RewardController(
    private val rewardUseCase: RewardUseCase,
    private val responseMapper: ResponseMapper
) {

    /**
     * 리워드 페이지 전체 데이터 조회
     * GET /api/v1/rewards/page?userId={userId}
     */
    @GetMapping("/page")
    fun getRewardsPage(
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<RewardsPageResponse>> {
        val userPoints = rewardUseCase.getUserPoints(UserId(userId))
        val availableRewards = rewardUseCase.getAvailableRewards(
            AvailableRewardsQuery(userId = userId, page = 0, size = 50)
        )
        val rewardHistory = rewardUseCase.getUserRewards(
            UserRewardsQuery(userId = userId, page = 0, size = 20)
        )

        val response = RewardsPageResponse(
            userPoints = responseMapper.toUserPointsResponse(userPoints),
            availableRewards = availableRewards.map { responseMapper.toRewardResponse(it) },
            rewardHistory = rewardHistory.map { responseMapper.toUserRewardResponse(it) }
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 사용 가능한 리워드 조회
     * GET /api/v1/rewards?userId={userId}&category={category}&page={page}&size={size}
     */
    @GetMapping
    fun getAvailableRewards(
        @RequestParam userId: Long,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) maxPoints: Int?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<RewardResponse>>> {
        val query = AvailableRewardsQuery(userId, category, maxPoints, page, size)
        val rewards = rewardUseCase.getAvailableRewards(query)
        val rewardResponses = rewards.map { responseMapper.toRewardResponse(it) }

        // 임시 페이징 정보
        val pageInfo = PageInfo(
            currentPage = page,
            pageSize = size,
            totalElements = rewards.size.toLong(),
            totalPages = (rewards.size / size) + 1,
            hasNext = rewards.size >= size,
            hasPrevious = page > 0
        )

        val pagedResponse = PagedResponse(rewardResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 리워드 상세 조회
     * GET /api/v1/rewards/{rewardId}
     */
    @GetMapping("/{rewardId}")
    fun getReward(
        @PathVariable rewardId: Long
    ): ResponseEntity<ApiResponse<RewardResponse>> {
        val reward = rewardUseCase.getReward(RewardId(rewardId))
            ?: return ResponseEntity.ok(ApiResponse.error("Reward not found", "REWARD_NOT_FOUND"))

        val response = responseMapper.toRewardResponse(reward)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 리워드 교환
     * POST /api/v1/rewards/{rewardId}/exchange
     */
    @PostMapping("/{rewardId}/exchange")
    fun exchangeReward(
        @PathVariable rewardId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<UserRewardResponse>> {
        val command = ExchangeRewardCommand(userId, rewardId)
        val userReward = rewardUseCase.exchangeReward(command)
        val response = responseMapper.toUserRewardResponse(userReward)

        return ResponseEntity.ok(ApiResponse.success(response, "리워드가 성공적으로 교환되었습니다."))
    }

    /**
     * 사용자 리워드 내역 조회
     * GET /api/v1/rewards/my-rewards?userId={userId}&status={status}&page={page}&size={size}
     */
    @GetMapping("/my-rewards")
    fun getUserRewards(
        @RequestParam userId: Long,
        @RequestParam(required = false) status: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<UserRewardResponse>>> {
        val query = UserRewardsQuery(userId, status, page, size)
        val userRewards = rewardUseCase.getUserRewards(query)
        val rewardResponses = userRewards.map { responseMapper.toUserRewardResponse(it) }

        // 임시 페이징 정보
        val pageInfo = PageInfo(
            currentPage = page,
            pageSize = size,
            totalElements = userRewards.size.toLong(),
            totalPages = (userRewards.size / size) + 1,
            hasNext = userRewards.size >= size,
            hasPrevious = page > 0
        )

        val pagedResponse = PagedResponse(rewardResponses, pageInfo)
        return ResponseEntity.ok(ApiResponse.success(pagedResponse))
    }

    /**
     * 리워드 사용
     * POST /api/v1/rewards/my-rewards/{userRewardId}/use
     */
    @PostMapping("/my-rewards/{userRewardId}/use")
    fun useReward(
        @PathVariable userRewardId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<UserRewardResponse>> {
        val command = UseRewardCommand(userRewardId, userId)
        val userReward = rewardUseCase.useReward(command)
        val response = responseMapper.toUserRewardResponse(userReward)

        return ResponseEntity.ok(ApiResponse.success(response, "리워드가 사용되었습니다."))
    }

    /**
     * 사용자 포인트 정보 조회
     * GET /api/v1/rewards/points?userId={userId}
     */
    @GetMapping("/points")
    fun getUserPoints(@RequestParam userId: Long): ResponseEntity<ApiResponse<UserPointsResponse>> {
        val userPoints = rewardUseCase.getUserPoints(UserId(userId))
        val response = responseMapper.toUserPointsResponse(userPoints)

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 인기 리워드 조회
     * GET /api/v1/rewards/popular?limit={limit}
     */
    @GetMapping("/popular")
    fun getPopularRewards(
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<RewardResponse>>> {
        val rewards = rewardUseCase.getPopularRewards(limit)
        val response = rewards.map { responseMapper.toRewardResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 카테고리별 리워드 조회
     * GET /api/v1/rewards/category/{category}
     */
    @GetMapping("/category/{category}")
    fun getRewardsByCategory(@PathVariable category: String): ResponseEntity<ApiResponse<List<RewardResponse>>> {
        val rewardCategory = RewardCategory.valueOf(category.uppercase())
        val rewards = rewardUseCase.getRewardsByCategory(rewardCategory)
        val response = rewards.map { responseMapper.toRewardResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 만료 임박 리워드 조회
     * GET /api/v1/rewards/expiring?userId={userId}&withinDays={withinDays}
     */
    @GetMapping("/expiring")
    fun getExpiringRewards(
        @RequestParam userId: Long,
        @RequestParam(defaultValue = "7") withinDays: Int
    ): ResponseEntity<ApiResponse<List<UserRewardResponse>>> {
        val userRewards = rewardUseCase.getExpiringRewards(UserId(userId), withinDays)
        val response = userRewards.map { responseMapper.toUserRewardResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    /**
     * 리워드 통계 조회
     * GET /api/v1/rewards/statistics?userId={userId}
     */
    @GetMapping("/statistics")
    fun getRewardStatistics(
        @RequestParam userId: Long
    ): ResponseEntity<ApiResponse<RewardStatistics>> {
        val statistics = rewardUseCase.getRewardStatistics(UserId(userId))
        return ResponseEntity.ok(ApiResponse.success(statistics))
    }

}