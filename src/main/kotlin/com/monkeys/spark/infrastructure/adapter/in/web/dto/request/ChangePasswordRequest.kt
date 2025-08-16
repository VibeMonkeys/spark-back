package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)