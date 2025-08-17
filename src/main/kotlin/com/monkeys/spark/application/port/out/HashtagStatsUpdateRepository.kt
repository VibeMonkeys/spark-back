package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.HashtagStats
import com.monkeys.spark.domain.vo.story.HashTag
import java.time.LocalDate

/**
 * 해시태그 통계 업데이트 리포지토리 포트
 */
interface HashtagStatsUpdateRepository {
    
    /**
     * 해시태그 사용량 증가
     */
    fun incrementHashtagUsage(hashtag: HashTag, date: LocalDate = LocalDate.now()): HashtagStats
    
    /**
     * 여러 해시태그 사용량 일괄 증가
     */
    fun incrementHashtagsUsage(hashtags: List<HashTag>, date: LocalDate = LocalDate.now()): List<HashtagStats>
    
    /**
     * 해시태그 통계가 존재하는지 확인
     */
    fun existsByHashtagAndDate(hashtag: HashTag, date: LocalDate): Boolean
    
    /**
     * 해시태그 통계 생성 또는 업데이트
     */
    fun createOrUpdateHashtagStats(hashtag: HashTag, date: LocalDate = LocalDate.now()): HashtagStats
}