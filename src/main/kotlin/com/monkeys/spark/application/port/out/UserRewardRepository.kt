package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.UserReward
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.UserRewardId

interface UserRewardRepository {

    fun save(userReward: UserReward): UserReward

    fun findById(userRewardId: UserRewardId): UserReward?

    fun findByUserId(userId: UserId): List<UserReward>

    fun findExpiringWithinDays(days: Int): List<UserReward>

    fun getTotalPointsSpentByUserId(userId: UserId): Points

    fun getThisMonthPointsSpentByUserId(userId: UserId): Points

}