package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*

/**
 * 유사 미션 Value Object
 */
data class SimilarMission(
    val id: MissionId,
    val title: MissionTitle,
    val difficulty: MissionDifficulty,
    val points: Points,
    val category: MissionCategory
)