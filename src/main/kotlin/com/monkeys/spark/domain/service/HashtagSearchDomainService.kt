package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.HashtagStats
import com.monkeys.spark.domain.model.Story
import com.monkeys.spark.domain.vo.story.HashTag
import com.monkeys.spark.domain.vo.hashtag.HashtagCategory
import com.monkeys.spark.domain.vo.hashtag.HashtagSortCriteria
import java.time.LocalDate

/**
 * 해시태그 검색 도메인 서비스
 * 해시태그 검색과 관련된 순수 비즈니스 로직을 담당
 */
class HashtagSearchDomainService {
    
    /**
     * 해시태그 유사도 계산
     * 레벤슈타인 거리 기반 유사도 점수 반환 (0.0 ~ 1.0)
     */
    fun calculateHashtagSimilarity(hashtag1: HashTag, hashtag2: HashTag): Double {
        val str1 = hashtag1.value.lowercase()
        val str2 = hashtag2.value.lowercase()
        
        if (str1 == str2) return 1.0
        
        val distance = levenshteinDistance(str1, str2)
        val maxLength = maxOf(str1.length, str2.length)
        
        return if (maxLength == 0) 1.0 else 1.0 - (distance.toDouble() / maxLength)
    }
    
    /**
     * 해시태그 검색 점수 계산
     * 유사도, 인기도, 최근성을 종합하여 검색 점수 산출
     */
    fun calculateSearchScore(
        searchQuery: String,
        hashtagStats: HashtagStats,
        similarityWeight: Double = 0.5,
        popularityWeight: Double = 0.3,
        recencyWeight: Double = 0.2
    ): Double {
        // 1. 유사도 점수 (검색어와 해시태그의 유사도)
        val queryHashtag = HashTag(if (searchQuery.startsWith("#")) searchQuery else "#$searchQuery")
        val similarityScore = calculateHashtagSimilarity(queryHashtag, hashtagStats.hashtag)
        
        // 2. 인기도 점수 (0.0 ~ 1.0으로 정규화)
        val popularityScore = normalizePopularityScore(hashtagStats.trendScore)
        
        // 3. 최근성 점수 (최근 사용일 기준)
        val recencyScore = calculateRecencyScore(hashtagStats)
        
        // 가중 평균으로 최종 점수 계산
        return (similarityScore * similarityWeight) + 
               (popularityScore * popularityWeight) + 
               (recencyScore * recencyWeight)
    }
    
    /**
     * 해시태그 자동완성 후보 필터링
     * 입력된 prefix에 따라 적절한 해시태그 후보들을 필터링
     */
    fun filterAutocompleteCandidates(
        prefix: String,
        candidates: List<HashtagStats>,
        maxResults: Int = 10
    ): List<HashtagStats> {
        val normalizedPrefix = if (prefix.startsWith("#")) prefix.lowercase() else "#${prefix.lowercase()}"
        val prefixWithoutHash = normalizedPrefix.removePrefix("#")
        
        return candidates
            .filter { stats ->
                val hashtag = stats.hashtag.value.lowercase()
                val hashtagWithoutHash = hashtag.removePrefix("#")
                
                // 더 엄격한 필터링 기준
                // 1. 정확한 prefix 매치 (가장 우선)
                hashtag.startsWith(normalizedPrefix) ||
                // 2. # 없이 prefix 매치
                hashtagWithoutHash.startsWith(prefixWithoutHash) ||
                // 3. 단어 시작 부분 매치 (예: "카페"로 검색했을 때 "카페음료" 매치)
                hashtagWithoutHash.contains(prefixWithoutHash) && 
                hashtagWithoutHash.indexOf(prefixWithoutHash) <= 2 // 앞부분에 위치
            }
            .sortedWith(compareBy<HashtagStats> { stats ->
                val hashtag = stats.hashtag.value.lowercase()
                val hashtagWithoutHash = hashtag.removePrefix("#")
                
                when {
                    // 1순위: 정확한 prefix 매치
                    hashtag.startsWith(normalizedPrefix) -> 0
                    // 2순위: # 없이 정확한 매치
                    hashtagWithoutHash.startsWith(prefixWithoutHash) -> 1
                    // 3순위: 부분 포함
                    hashtagWithoutHash.contains(prefixWithoutHash) -> 2
                    else -> 3
                }
            }.thenByDescending { it.totalCount }) // 사용량 순
            .take(maxResults)
    }
    
    /**
     * 해시태그 카테고리 분류
     * 해시태그 내용을 분석하여 적절한 카테고리로 분류
     */
    fun categorizeHashtag(hashtag: HashTag): HashtagCategory {
        val content = hashtag.value.lowercase()
        
        return when {
            // 건강 관련
            content.contains("운동") || content.contains("헬스") || content.contains("건강") || 
            content.contains("요가") || content.contains("스포츠") || content.contains("달리기") ||
            content.contains("산책") || content.contains("계단") -> HashtagCategory.HEALTH
            
            // 음식 관련  
            content.contains("음식") || content.contains("카페") || content.contains("맛집") ||
            content.contains("커피") || content.contains("맛있") || content.contains("요리") ||
            content.contains("먹") || content.contains("식당") -> HashtagCategory.FOOD
            
            // 여행/모험 관련
            content.contains("여행") || content.contains("모험") || content.contains("탐험") ||
            content.contains("등산") || content.contains("바다") || content.contains("산") ||
            content.contains("공원") || content.contains("새로운") -> HashtagCategory.ADVENTURE
            
            // 소셜 관련
            content.contains("친구") || content.contains("사람") || content.contains("소셜") ||
            content.contains("만남") || content.contains("대화") || content.contains("파티") ||
            content.contains("모임") -> HashtagCategory.SOCIAL
            
            // 학습 관련
            content.contains("학습") || content.contains("공부") || content.contains("독서") ||
            content.contains("책") || content.contains("강의") || content.contains("배우") ||
            content.contains("영어") || content.contains("언어") -> HashtagCategory.LEARNING
            
            // 창의 관련
            content.contains("창의") || content.contains("예술") || content.contains("그림") ||
            content.contains("음악") || content.contains("사진") || content.contains("글쓰") ||
            content.contains("만들") -> HashtagCategory.CREATIVE
            
            // 일상 관련
            content.contains("일상") || content.contains("평일") || content.contains("주말") ||
            content.contains("아침") || content.contains("저녁") || content.contains("시간") ||
            content.contains("오늘") -> HashtagCategory.DAILY
            
            else -> HashtagCategory.OTHER
        }
    }
    
    /**
     * 스토리에서 해시태그 추출 및 정규화
     */
    fun extractAndNormalizeHashtags(story: Story): Set<HashTag> {
        val allTags = mutableSetOf<HashTag>()
        
        // 사용자 태그 추가
        allTags.addAll(story.userTags)
        
        // 자동 태그 추가
        allTags.addAll(story.autoTags)
        
        return allTags
    }
    
    /**
     * 해시태그 트렌드 점수 계산
     * 시간대별 가중치를 적용한 트렌드 점수 계산
     */
    fun calculateTrendScore(
        dailyUsage: Int,
        weeklyUsage: Int,
        monthlyUsage: Int,
        recencyHours: Long
    ): Double {
        // 최근성 가중치 (최근 사용할수록 높은 점수)
        val recencyWeight = when {
            recencyHours <= 1 -> 3.0
            recencyHours <= 6 -> 2.0
            recencyHours <= 24 -> 1.5
            recencyHours <= 168 -> 1.0 // 7일
            else -> 0.5
        }
        
        // 사용량 기반 점수
        val usageScore = (dailyUsage * 1.0) + (weeklyUsage * 0.3) + (monthlyUsage * 0.1)
        
        return usageScore * recencyWeight
    }
    
    /**
     * 해시태그 검색 결과 정렬
     */
    fun sortSearchResults(
        hashtags: List<HashtagStats>,
        searchQuery: String,
        sortBy: HashtagSortCriteria = HashtagSortCriteria.RELEVANCE
    ): List<HashtagStats> {
        return when (sortBy) {
            HashtagSortCriteria.RELEVANCE -> {
                hashtags.sortedByDescending { calculateSearchScore(searchQuery, it) }
            }
            HashtagSortCriteria.POPULARITY -> {
                hashtags.sortedByDescending { it.trendScore }
            }
            HashtagSortCriteria.RECENT -> {
                hashtags.sortedByDescending { it.lastUsedAt }
            }
            HashtagSortCriteria.ALPHABETICAL -> {
                hashtags.sortedBy { it.hashtag.value }
            }
            HashtagSortCriteria.USAGE_COUNT -> {
                hashtags.sortedByDescending { it.totalCount }
            }
        }
    }
    
    // === Private Helper Methods ===
    
    private fun levenshteinDistance(str1: String, str2: String): Int {
        val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }
        
        for (i in 0..str1.length) dp[i][0] = i
        for (j in 0..str2.length) dp[0][j] = j
        
        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                dp[i][j] = if (str1[i - 1] == str2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
                }
            }
        }
        
        return dp[str1.length][str2.length]
    }
    
    private fun normalizePopularityScore(trendScore: Double): Double {
        // 트렌드 스코어를 0.0 ~ 1.0 범위로 정규화
        // 최대 트렌드 스코어를 100으로 가정
        return (trendScore / 100.0).coerceIn(0.0, 1.0)
    }
    
    private fun calculateRecencyScore(hashtagStats: HashtagStats): Double {
        val now = java.time.LocalDateTime.now()
        val hoursSinceLastUsed = java.time.Duration.between(hashtagStats.lastUsedAt, now).toHours()
        
        return when {
            hoursSinceLastUsed <= 1 -> 1.0
            hoursSinceLastUsed <= 6 -> 0.8
            hoursSinceLastUsed <= 24 -> 0.6
            hoursSinceLastUsed <= 168 -> 0.4 // 7일
            else -> 0.2
        }
    }
}