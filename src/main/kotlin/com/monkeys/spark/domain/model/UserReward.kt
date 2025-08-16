package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.RewardId
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.UserRewardId
import com.monkeys.spark.domain.vo.reward.BrandName
import com.monkeys.spark.domain.vo.reward.RewardStatus
import com.monkeys.spark.domain.vo.reward.RewardTitle
import java.time.LocalDateTime

/**
 * User Reward - represents a reward that has been exchanged by a user
 */
data class UserReward(
    var id: UserRewardId,
    var userId: UserId,
    var rewardId: RewardId,
    var rewardTitle: RewardTitle,
    var rewardBrand: BrandName,
    var pointsUsed: Points,
    var exchangeCode: String,
    var status: RewardStatus = RewardStatus.AVAILABLE,
    var expiresAt: LocalDateTime,
    var usedAt: LocalDateTime? = null,
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            userId: UserId,
            reward: Reward,
            pointsUsed: Points
        ): UserReward {
            val exchangeCode = generateExchangeCode(reward.brand.value)
            val expiresAt = reward.expirationDays.toExpirationDate()

            return UserReward(
                id = UserRewardId.generate(), // ID는 DB에서 자동 생성되므로 초기값은 0
                userId = userId,
                rewardId = reward.id,
                rewardTitle = reward.title,
                rewardBrand = reward.brand,
                pointsUsed = pointsUsed,
                exchangeCode = exchangeCode,
                expiresAt = expiresAt
            )
        }

        private fun generateExchangeCode(brandName: String): String {
            val prefix = when (brandName.lowercase()) {
                "스타벅스" -> "STBK"
                "gs25" -> "GS25"
                "투썸플레이스" -> "TSOM"
                "cgv" -> "CGV"
                "monkeys" -> "MNKY"
                else -> "RWRD"
            }
            val randomNumber = (1000..9999).random()
            val randomSuffix = (1000..9999).random()
            return "$prefix-$randomNumber-$randomSuffix"
        }
    }

    fun use(): UserReward {
        require(status == RewardStatus.AVAILABLE) { "Reward must be available to use" }
        require(!isExpired()) { "Cannot use expired reward" }

        status = RewardStatus.USED
        usedAt = LocalDateTime.now()
        return this
    }

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

    fun getTimeUntilExpiration(): String {
        if (isExpired()) return "만료됨"

        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(now, expiresAt)

        return when {
            duration.toDays() > 0 -> "${duration.toDays()}일 남음"
            duration.toHours() > 0 -> "${duration.toHours()}시간 남음"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}분 남음"
            else -> "곧 만료"
        }
    }

    fun getUsageStatusText(): String {
        return when (status) {
            RewardStatus.AVAILABLE -> if (isExpired()) "만료됨" else "사용 가능"
            RewardStatus.USED -> "사용됨"
            RewardStatus.EXPIRED -> "만료됨"
        }
    }

    fun getUsageTimeText(): String {
        return when (status) {
            RewardStatus.USED -> usedAt?.let { getTimeAgo(it) } ?: ""
            RewardStatus.EXPIRED -> "만료됨"
            RewardStatus.AVAILABLE -> getTimeUntilExpiration()
        }
    }

    private fun getTimeAgo(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(dateTime, now)

        return when {
            duration.toDays() > 0 -> "${duration.toDays()}일 전"
            duration.toHours() > 0 -> "${duration.toHours()}시간 전"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}분 전"
            else -> "방금 전"
        }
    }
}