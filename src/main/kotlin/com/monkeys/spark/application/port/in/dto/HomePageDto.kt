package com.monkeys.spark.application.port.`in`.dto

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.model.User

/**
 * 홈페이지 전체 데이터
 */
data class HomePageData(
    val userSummary: UserSummary,
    val todaysMissions: List<Mission>,
    val recentStories: List<Story>
)

/**
 * 사용자 요약 정보
 */
data class UserSummary(
    val user: User,
    val progressToNextLevel: Int, // 0-100 percentage
    val pointsToNextLevel: Int
)