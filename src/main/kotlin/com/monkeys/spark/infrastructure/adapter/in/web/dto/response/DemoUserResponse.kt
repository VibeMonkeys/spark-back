package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

data class DemoUserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val level: Int,
    val levelTitle: String,
    val currentPoints: Int,
    val totalPoints: Int,
    val avatarUrl: String
)