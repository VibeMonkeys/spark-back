package com.monkeys.spark.domain.vo.user

import com.monkeys.spark.domain.vo.user.UserLevelTitle

/**
 * 레벨 시스템 도메인 모델
 * 각 레벨의 상세 정보와 요구사항을 관리
 */
data class LevelInfo(
    val level: Int,
    val levelTitle: UserLevelTitle,
    val requiredPoints: Int,
    val nextLevelPoints: Int?,
    val description: String,
    val benefits: List<String>,
    val icon: String,
    val color: String,
    val badge: String
)