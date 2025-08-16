package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.monkeys.spark.domain.model.User

/**
 * 인증 결과를 담는 데이터 클래스
 */
data class AuthResult(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)