package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.HashtagStats
import com.monkeys.spark.domain.vo.common.HashtagStatsId
import com.monkeys.spark.domain.vo.story.HashTag
import java.time.LocalDate

/**
 * 해시태그 통계 리포지토리 인터페이스 (아웃바운드 포트)
 */
interface HashtagStatsRepository {
    
    /**
     * 해시태그 통계 저장
     */
    fun save(hashtagStats: HashtagStats): HashtagStats
    
    /**
     * ID로 해시태그 통계 조회
     */
    fun findById(id: HashtagStatsId): HashtagStats?
    
    /**
     * 특정 해시태그와 날짜로 통계 조회
     */
    fun findByHashtagAndDate(hashtag: HashTag, date: LocalDate): HashtagStats?
    
    /**
     * 특정 해시태그의 모든 통계 조회 (최신순)
     */
    fun findByHashtagOrderByDateDesc(hashtag: HashTag): List<HashtagStats>
    
    /**
     * 특정 날짜의 트렌딩 해시태그 조회
     */
    fun findTrendingHashtags(date: LocalDate, limit: Int): List<HashtagStats>
    
    /**
     * 특정 날짜의 인기 해시태그 조회
     */
    fun findPopularHashtags(date: LocalDate, limit: Int): List<HashtagStats>
    
    /**
     * 해시태그 자동완성을 위한 검색
     */
    fun findHashtagsStartingWith(prefix: String, date: LocalDate, limit: Int): List<HashtagStats>
    
    /**
     * 특정 기간의 해시태그 통계 조회
     */
    fun findByHashtagAndDateBetween(hashtag: HashTag, startDate: LocalDate, endDate: LocalDate): List<HashtagStats>
    
    /**
     * 카테고리별 인기 해시태그 조회
     */
    fun findByCategoryKeywords(date: LocalDate, keywords: List<String>, limit: Int): List<HashtagStats>
    
    /**
     * 특정 날짜의 모든 해시태그 통계 조회 (트렌드 스코어 내림차순)
     */
    fun findByDateOrderByTrendScore(date: LocalDate, limit: Int): List<HashtagStats>
    
    /**
     * 특정 날짜의 모든 해시태그 통계 조회 (일일 사용량 내림차순)
     */
    fun findByDateOrderByDailyCount(date: LocalDate, limit: Int): List<HashtagStats>
    
    /**
     * 전체 해시태그 통계 요약 조회
     */
    fun getStatsSummary(date: LocalDate): HashtagStatsSummary
    
    /**
     * 모든 해시태그의 일일 카운트 초기화
     */
    fun resetDailyCountsForDate(date: LocalDate)
    
    /**
     * 모든 해시태그의 주간 카운트 초기화
     */
    fun resetWeeklyCountsForDate(date: LocalDate)
    
    /**
     * 모든 해시태그의 월간 카운트 초기화
     */
    fun resetMonthlyCountsForDate(date: LocalDate)
    
    /**
     * 성장률 계산을 위한 이전 주 데이터 조회
     */
    fun findPreviousWeekStats(previousWeekDate: LocalDate, limit: Int): List<HashtagStats>
    
    /**
     * 최신 날짜의 모든 해시태그 조회 (자동완성용)
     */
    fun findAllHashtagsFromLatestDate(limit: Int): List<String>
    
    /**
     * 해시태그 통계 삭제
     */
    fun deleteById(id: HashtagStatsId)
    
    /**
     * 특정 날짜 이전의 오래된 통계 삭제
     */
    fun deleteOldStats(beforeDate: LocalDate)
}

/**
 * 해시태그 통계 요약 정보
 */
data class HashtagStatsSummary(
    val totalHashtags: Long,
    val totalDailyUsage: Long,
    val averageTrendScore: Double
)