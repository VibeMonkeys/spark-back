package com.monkeys.spark.domain.vo.common

import java.util.*

// 공통 ID Value Objects
@JvmInline
value class UserId(val value: Long) {
    companion object {
        fun generate(): UserId = UserId(0L) // Auto-increment에 의해 생성됨
    }
}

@JvmInline
value class MissionId(val value: Long) {
    companion object {
        fun generate(): MissionId = MissionId(0L) // Auto-increment에 의해 생성뜨
    }
}

@JvmInline
value class StoryId(val value: Long) {
    companion object {
        fun generate(): StoryId = StoryId(0L) // Auto-increment에 의해 생성됨
    }
}

@JvmInline
value class RewardId(val value: Long) {
    companion object {
        fun generate(): RewardId = RewardId(0L) // Auto-increment에 의해 생성됨
    }
}

@JvmInline
value class UserRewardId(val value: Long) {
    companion object {
        fun generate(): UserRewardId = UserRewardId(0L) // Auto-increment에 의해 생성됨
    }
}