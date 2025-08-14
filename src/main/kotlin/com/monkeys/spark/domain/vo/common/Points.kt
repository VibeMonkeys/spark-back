package com.monkeys.spark.domain.vo.common

// 포인트 관련 Value Object
@JvmInline
value class Points(val value: Int) {
    init {
        require(value >= 0) { "Points cannot be negative" }
    }
    
    operator fun plus(other: Points): Points = Points(value + other.value)
    operator fun minus(other: Points): Points = Points(maxOf(0, value - other.value))
    operator fun compareTo(other: Points): Int = value.compareTo(other.value)
}