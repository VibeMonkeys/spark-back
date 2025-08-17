package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.HashtagStatsId
import com.monkeys.spark.domain.vo.story.HashTag
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 해시태그 통계 도메인 모델
 * 해시태그별 사용 빈도, 트렌드 분석을 위한 Aggregate Root
 */
data class HashtagStats(
    val id: HashtagStatsId,
    val hashtag: HashTag,
    var dailyCount: Int = 0,
    var weeklyCount: Int = 0,
    var monthlyCount: Int = 0,
    var totalCount: Int = 0,
    var lastUsedAt: LocalDateTime = LocalDateTime.now(),
    val date: LocalDate = LocalDate.now(),
    var trendScore: Double = 0.0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        /**
         * 새로운 해시태그 통계 생성
         */
        fun create(hashtag: HashTag, date: LocalDate = LocalDate.now()): HashtagStats {
            return HashtagStats(
                id = HashtagStatsId.generate(),
                hashtag = hashtag,
                date = date,
                dailyCount = 1,
                weeklyCount = 1,
                monthlyCount = 1,
                totalCount = 1,
                lastUsedAt = LocalDateTime.now(),
                trendScore = calculateInitialTrendScore()
            )
        }

        /**
         * 초기 트렌드 스코어 계산
         */
        private fun calculateInitialTrendScore(): Double {
            return 1.0 // 기본값
        }
    }

    /**
     * 사용 횟수 증가
     */
    fun incrementUsage() {
        dailyCount++
        weeklyCount++
        monthlyCount++
        totalCount++
        lastUsedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
        updateTrendScore()
    }

    /**
     * 트렌드 스코어 업데이트
     * 최근 사용 빈도와 총 사용량을 기반으로 계산
     */
    private fun updateTrendScore() {
        val now = LocalDateTime.now()
        val hoursSinceLastUsed = java.time.Duration.between(lastUsedAt, now).toHours()
        
        // 최근성 가중치 (최근 사용할수록 높은 점수)
        val recencyWeight = when {
            hoursSinceLastUsed <= 1 -> 2.0
            hoursSinceLastUsed <= 6 -> 1.5
            hoursSinceLastUsed <= 24 -> 1.0
            hoursSinceLastUsed <= 168 -> 0.5 // 7일
            else -> 0.1
        }
        
        // 빈도 가중치 (사용량이 많을수록 높은 점수)
        val frequencyWeight = when {
            dailyCount >= 50 -> 3.0
            dailyCount >= 20 -> 2.5
            dailyCount >= 10 -> 2.0
            dailyCount >= 5 -> 1.5
            else -> 1.0
        }
        
        trendScore = (dailyCount * recencyWeight * frequencyWeight) + 
                    (weeklyCount * 0.3) + 
                    (monthlyCount * 0.1)
    }

    /**
     * 일일 통계 초기화 (매일 00:00에 실행)
     */
    fun resetDailyStats() {
        dailyCount = 0
        updatedAt = LocalDateTime.now()
        updateTrendScore()
    }

    /**
     * 주간 통계 초기화 (매주 월요일 00:00에 실행)
     */
    fun resetWeeklyStats() {
        weeklyCount = 0
        updatedAt = LocalDateTime.now()
        updateTrendScore()
    }

    /**
     * 월간 통계 초기화 (매월 1일 00:00에 실행)
     */
    fun resetMonthlyStats() {
        monthlyCount = 0
        updatedAt = LocalDateTime.now()
        updateTrendScore()
    }

    /**
     * 인기 해시태그 여부 확인
     */
    fun isPopular(): Boolean {
        return dailyCount >= 10 || weeklyCount >= 50 || trendScore >= 10.0
    }

    /**
     * 트렌딩 해시태그 여부 확인
     */
    fun isTrending(): Boolean {
        return trendScore >= 20.0 && dailyCount >= 5
    }

    /**
     * 해시태그 카테고리 추론
     */
    fun inferCategory(): String {
        val tagValue = hashtag.value.lowercase()
        return when {
            tagValue.contains("운동") || tagValue.contains("헬스") || tagValue.contains("건강") -> "건강"
            tagValue.contains("여행") || tagValue.contains("모험") || tagValue.contains("탐험") -> "여행/모험"
            tagValue.contains("음식") || tagValue.contains("카페") || tagValue.contains("맛집") -> "음식"
            tagValue.contains("친구") || tagValue.contains("사람") || tagValue.contains("소셜") -> "소셜"
            tagValue.contains("학습") || tagValue.contains("공부") || tagValue.contains("독서") -> "학습"
            tagValue.contains("창의") || tagValue.contains("예술") || tagValue.contains("그림") -> "창의"
            tagValue.contains("일상") || tagValue.contains("평일") || tagValue.contains("주말") -> "일상"
            else -> "기타"
        }
    }

    /**
     * 성장률 계산 (일주일 전 대비)
     */
    fun calculateGrowthRate(previousWeeklyCount: Int): Double {
        return if (previousWeeklyCount > 0) {
            ((weeklyCount - previousWeeklyCount).toDouble() / previousWeeklyCount) * 100
        } else {
            100.0 // 새로운 해시태그의 경우 100% 성장
        }
    }
}