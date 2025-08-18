package com.monkeys.spark.domain.vo.hashtag

import com.monkeys.spark.domain.model.HashtagStats

/**
 * 카테고리별 집계 정보
 */
data class CategoryAggregation(
    val totalHashtags: Int,
    val totalUsage: Int,
    val averageTrendScore: Double,
    val topHashtags: List<HashtagStats>
)