package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.dto.HomePageData
import com.monkeys.spark.application.port.`in`.dto.UserSummary
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.common.UserId

/**
 * 홈페이지 관련 UseCase 인터페이스
 */
interface HomePageUseCase {
    
    /**
     * 홈페이지 전체 데이터 조회
     */
    fun getHomePageData(userId: UserId): HomePageData
    
    /**
     * 사용자 요약 정보 조회
     */
    fun getUserSummary(userId: UserId): UserSummary
    
    /**
     * 오늘의 추천 미션 조회 (3개)
     */
    fun getTodaysRecommendedMissions(userId: UserId): List<Mission>
    
    /**
     * 최근 스토리 조회 (홈페이지용)
     */
    fun getRecentStoriesForHome(limit: Int): List<Story>
}