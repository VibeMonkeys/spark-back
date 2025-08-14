package com.monkeys.spark.domain.vo.common

import java.util.*

// 공통 ID Value Objects
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class MissionId(val value: String) {
    companion object {
        fun generate(): MissionId = MissionId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class StoryId(val value: String) {
    companion object {
        fun generate(): StoryId = StoryId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class RewardId(val value: String) {
    companion object {
        fun generate(): RewardId = RewardId(UUID.randomUUID().toString())
    }
}