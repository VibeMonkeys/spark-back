package com.monkeys.spark.application.coordinator

import com.monkeys.spark.application.port.out.HashtagStatsUpdateRepository
import com.monkeys.spark.domain.vo.story.HashTag
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 스토리와 해시태그 통계 연동 코디네이터
 * 스토리 생성/수정 시 해시태그 통계 업데이트를 담당
 * Repository 의존성이 필요한 해시태그 통계 처리를 관리
 */
@Component
@Transactional
class StoryHashtagCoordinator(
    private val hashtagStatsUpdateRepository: HashtagStatsUpdateRepository
) {

    /**
     * 스토리 생성/수정 시 해시태그 통계 업데이트
     */
    fun updateHashtagStatsForStory(hashtags: List<HashTag>) {
        if (hashtags.isEmpty()) return

        val today = LocalDate.now()
        val uniqueHashtags = hashtags.distinct()

        // 각 해시태그에 대해 통계 업데이트 또는 생성
        uniqueHashtags.forEach { hashtag ->
            updateOrCreateHashtagStats(hashtag, today)
        }
    }

    /**
     * 단일 해시태그 통계 업데이트 또는 생성
     */
    private fun updateOrCreateHashtagStats(hashtag: HashTag, date: LocalDate) {
        if (hashtagStatsUpdateRepository.existsByHashtagAndDate(hashtag, date)) {
            // 기존 통계 업데이트
            hashtagStatsUpdateRepository.incrementHashtagUsage(hashtag, date)
        } else {
            // 새로운 통계 생성
            hashtagStatsUpdateRepository.createOrUpdateHashtagStats(hashtag, date)
        }
    }

    /**
     * 여러 해시태그 일괄 업데이트
     */
    fun batchUpdateHashtagStats(hashtags: List<HashTag>) {
        updateHashtagStatsForStory(hashtags)
    }
}