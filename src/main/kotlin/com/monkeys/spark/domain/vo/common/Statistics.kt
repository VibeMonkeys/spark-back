package com.monkeys.spark.domain.vo.common

// 통계 관련 Value Objects
@JvmInline
value class CompletionRate(val value: Double) {
    init {
        require(value in 0.0..100.0) { "Completion rate must be between 0 and 100" }
    }
}

@JvmInline
value class Rating(val value: Double) {
    init {
        require(value in 0.0..5.0) { "Rating must be between 0 and 5" }
    }
}