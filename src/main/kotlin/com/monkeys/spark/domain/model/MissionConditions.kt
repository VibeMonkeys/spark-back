package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.Location

/**
 * 미션 조건 Value Object
 */
data class MissionConditions(
    var weatherCondition: String? = null,
    var timeOfDay: String? = null,
    var location: Location? = null,
    var season: String? = null,
    var minParticipants: Int? = null,
    var maxParticipants: Int? = null,
    var equipmentRequired: MutableList<String> = mutableListOf(),
    var ageRestriction: String? = null,
    var safetyLevel: String? = null
)