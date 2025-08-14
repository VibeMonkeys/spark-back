package com.monkeys.spark.domain.vo.common

// 위치 관련 Value Object
@JvmInline
value class Location(val value: String) {
    init {
        require(value.isNotBlank()) { "Location cannot be blank" }
        require(value.length <= 100) { "Location cannot exceed 100 characters" }
    }
}