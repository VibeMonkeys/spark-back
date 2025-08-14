package com.monkeys.spark.application.port.`in`.command

/**
 * 리워드 관련 Command 객체들
 */

/**
 * 리워드 교환 커맨드
 */
data class ExchangeRewardCommand(
    val userId: String,
    val rewardId: String
)

/**
 * 리워드 사용 커맨드
 */
data class UseRewardCommand(
    val userRewardId: String,
    val userId: String
)