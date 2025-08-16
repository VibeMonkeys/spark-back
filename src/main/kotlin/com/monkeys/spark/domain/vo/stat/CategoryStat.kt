package com.monkeys.spark.domain.vo.stat

import com.monkeys.spark.domain.vo.common.CompletionRate

/**
 * 카테고리별 통계 Value Object
 */
data class CategoryStat(
    val completed: Int = 0,
    val total: Int = 0
) {
    val percentage: CompletionRate
        get() = if (total > 0) CompletionRate((completed.toDouble() / total) * 100) else CompletionRate(0.0)
}