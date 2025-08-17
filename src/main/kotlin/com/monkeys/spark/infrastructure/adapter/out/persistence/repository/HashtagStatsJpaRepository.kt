package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.HashtagStatsEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * 해시태그 통계 JPA 리포지토리
 */
@Repository
interface HashtagStatsJpaRepository : JpaRepository<HashtagStatsEntity, String> {
    
    /**
     * 특정 해시태그와 날짜로 통계 조회
     */
    fun findByHashtagAndDate(hashtag: String, date: LocalDate): HashtagStatsEntity?
    
    /**
     * 특정 해시태그의 모든 통계 조회 (최신순)
     */
    fun findByHashtagOrderByDateDesc(hashtag: String): List<HashtagStatsEntity>
    
    /**
     * 특정 날짜의 모든 해시태그 통계 조회 (트렌드 스코어 내림차순)
     */
    fun findByDateOrderByTrendScoreDesc(date: LocalDate, pageable: Pageable): List<HashtagStatsEntity>
    
    /**
     * 특정 날짜의 모든 해시태그 통계 조회 (일일 사용량 내림차순)
     */
    fun findByDateOrderByDailyCountDesc(date: LocalDate, pageable: Pageable): List<HashtagStatsEntity>
    
    /**
     * 트렌딩 해시태그 조회 (트렌드 스코어 >= 20.0이고 일일 사용량 >= 5)
     */
    @Query("SELECT h FROM HashtagStatsEntity h WHERE h.date = :date AND h.trendScore >= 20.0 AND h.dailyCount >= 5 ORDER BY h.trendScore DESC")
    fun findTrendingHashtags(@Param("date") date: LocalDate, pageable: Pageable): List<HashtagStatsEntity>
    
    /**
     * 인기 해시태그 조회 (일일 사용량 >= 10 또는 주간 사용량 >= 50 또는 트렌드 스코어 >= 10.0)
     */
    @Query("SELECT h FROM HashtagStatsEntity h WHERE h.date = :date AND (h.dailyCount >= 10 OR h.weeklyCount >= 50 OR h.trendScore >= 10.0) ORDER BY h.dailyCount DESC")
    fun findPopularHashtags(@Param("date") date: LocalDate, pageable: Pageable): List<HashtagStatsEntity>
    
    /**
     * 해시태그 자동완성을 위한 검색 (해시태그가 prefix로 시작하는 것들을 총 사용량 내림차순으로)
     */
    @Query("SELECT h FROM HashtagStatsEntity h WHERE h.hashtag LIKE :prefix% AND h.date = :date ORDER BY h.totalCount DESC")
    fun findByHashtagStartingWithOrderByTotalCountDesc(
        @Param("prefix") prefix: String, 
        @Param("date") date: LocalDate, 
        pageable: Pageable
    ): List<HashtagStatsEntity>
    
    /**
     * 특정 기간의 해시태그 통계 조회
     */
    @Query("SELECT h FROM HashtagStatsEntity h WHERE h.hashtag = :hashtag AND h.date BETWEEN :startDate AND :endDate ORDER BY h.date DESC")
    fun findByHashtagAndDateBetween(
        @Param("hashtag") hashtag: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<HashtagStatsEntity>
    
    /**
     * 카테고리별 인기 해시태그 조회 (해시태그 내용 기반 필터링)
     */
    @Query("SELECT h FROM HashtagStatsEntity h WHERE h.date = :date AND (h.hashtag LIKE %:keyword1% OR h.hashtag LIKE %:keyword2% OR h.hashtag LIKE %:keyword3%) ORDER BY h.dailyCount DESC")
    fun findByCategoryKeywords(
        @Param("date") date: LocalDate,
        @Param("keyword1") keyword1: String,
        @Param("keyword2") keyword2: String,
        @Param("keyword3") keyword3: String,
        pageable: Pageable
    ): List<HashtagStatsEntity>
    
    /**
     * 전체 해시태그 통계 요약 (특정 날짜)
     */
    @Query("SELECT COUNT(h), SUM(h.dailyCount), AVG(h.trendScore) FROM HashtagStatsEntity h WHERE h.date = :date")
    fun getStatsSummary(@Param("date") date: LocalDate): Array<Any>
    
    /**
     * 모든 해시태그의 일일 카운트 초기화
     */
    @Modifying
    @Query("UPDATE HashtagStatsEntity h SET h.dailyCount = 0, h.updatedAt = CURRENT_TIMESTAMP WHERE h.date = :date")
    fun resetDailyCountsForDate(@Param("date") date: LocalDate)
    
    /**
     * 모든 해시태그의 주간 카운트 초기화
     */
    @Modifying
    @Query("UPDATE HashtagStatsEntity h SET h.weeklyCount = 0, h.updatedAt = CURRENT_TIMESTAMP WHERE h.date = :date")
    fun resetWeeklyCountsForDate(@Param("date") date: LocalDate)
    
    /**
     * 모든 해시태그의 월간 카운트 초기화
     */
    @Modifying
    @Query("UPDATE HashtagStatsEntity h SET h.monthlyCount = 0, h.updatedAt = CURRENT_TIMESTAMP WHERE h.date = :date")
    fun resetMonthlyCountsForDate(@Param("date") date: LocalDate)
    
    /**
     * 성장률 계산을 위한 이전 주 데이터 조회
     */
    @Query("SELECT h FROM HashtagStatsEntity h WHERE h.date = :previousWeekDate ORDER BY h.weeklyCount DESC")
    fun findPreviousWeekStats(@Param("previousWeekDate") previousWeekDate: LocalDate, pageable: Pageable): List<HashtagStatsEntity>
    
    /**
     * 최신 날짜의 모든 해시태그 조회 (자동완성용)
     */
    @Query("SELECT DISTINCT h.hashtag FROM HashtagStatsEntity h WHERE h.date = (SELECT MAX(h2.date) FROM HashtagStatsEntity h2) ORDER BY h.totalCount DESC")
    fun findAllHashtagsFromLatestDate(pageable: Pageable): List<String>
    
    /**
     * 해시태그와 날짜로 존재 여부 확인
     */
    fun existsByHashtagAndDate(hashtag: String, date: LocalDate): Boolean
    
    /**
     * 해시태그 사용량 증가 (일일, 주간, 월간, 전체)
     */
    @Modifying
    @Query("""
        UPDATE HashtagStatsEntity h 
        SET h.dailyCount = h.dailyCount + 1,
            h.weeklyCount = h.weeklyCount + 1,
            h.monthlyCount = h.monthlyCount + 1,
            h.totalCount = h.totalCount + 1,
            h.lastUsedAt = CURRENT_TIMESTAMP,
            h.updatedAt = CURRENT_TIMESTAMP
        WHERE h.hashtag = :hashtag AND h.date = :date
    """)
    fun incrementHashtagUsage(@Param("hashtag") hashtag: String, @Param("date") date: LocalDate): Int
    
    /**
     * 해시태그 트렌드 스코어 업데이트
     */
    @Modifying
    @Query("""
        UPDATE HashtagStatsEntity h 
        SET h.trendScore = :trendScore,
            h.updatedAt = CURRENT_TIMESTAMP
        WHERE h.hashtag = :hashtag AND h.date = :date
    """)
    fun updateTrendScore(@Param("hashtag") hashtag: String, @Param("date") date: LocalDate, @Param("trendScore") trendScore: Double)
}