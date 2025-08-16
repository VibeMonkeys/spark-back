package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface StoryJpaRepository : JpaRepository<StoryEntity, Long> {

    fun findByUserId(userId: Long): List<StoryEntity>

    fun findByMissionId(missionId: Long): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true ORDER BY s.createdAt DESC")
    fun findPublicStoriesOrderByCreatedDesc(pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true AND s.missionCategory = :category ORDER BY s.likeCount DESC")
    fun findPublicStoriesByCategory(@Param("category") category: String, pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true AND s.createdAt >= :startDate ORDER BY s.likeCount DESC")
    fun findTrendingStories(@Param("startDate") startDate: LocalDateTime, pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true AND (s.storyText LIKE %:keyword% OR s.userTags LIKE %:keyword%)")
    fun searchPublicStories(@Param("keyword") keyword: String, pageable: Pageable): List<StoryEntity>

    @Query("SELECT COUNT(s) FROM StoryEntity s WHERE s.userId = :userId")
    fun countStoriesByUser(@Param("userId") userId: String): Long

    @Query("SELECT s FROM StoryEntity s WHERE s.userId IN :userIds AND s.isPublic = true ORDER BY s.createdAt DESC")
    fun findStoriesFromUsers(@Param("userIds") userIds: List<String>, pageable: Pageable): List<StoryEntity>

    // StoryPersistenceAdapter에서 필요한 추가 메서드들
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): List<StoryEntity>

    fun findByMissionCategory(category: String): List<StoryEntity>

    fun findByLocationContaining(location: String): List<StoryEntity>

    fun findByHashTagsContaining(hashTag: String): List<StoryEntity>

    fun findByStoryTextContainingIgnoreCase(keyword: String): List<StoryEntity>

    fun findByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<StoryEntity>

    fun deleteByUserId(userId: Long)

    fun deleteByMissionId(missionId: Long)

    // 커서 기반 페이지네이션을 위한 메서드들
    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true AND s.id < :cursor ORDER BY s.id DESC")
    fun findPublicStoriesBeforeCursor(@Param("cursor") cursor: Long, pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true AND s.id > :cursor ORDER BY s.id ASC")
    fun findPublicStoriesAfterCursor(@Param("cursor") cursor: Long, pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.isPublic = true ORDER BY s.id DESC")
    fun findPublicStoriesWithoutCursor(pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.userId = :userId AND s.id < :cursor ORDER BY s.id DESC")
    fun findUserStoriesBeforeCursor(@Param("userId") userId: Long, @Param("cursor") cursor: Long, pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.userId = :userId AND s.id > :cursor ORDER BY s.id ASC")
    fun findUserStoriesAfterCursor(@Param("userId") userId: Long, @Param("cursor") cursor: Long, pageable: Pageable): List<StoryEntity>

    @Query("SELECT s FROM StoryEntity s WHERE s.userId = :userId ORDER BY s.id DESC")
    fun findUserStoriesWithoutCursor(@Param("userId") userId: Long, pageable: Pageable): List<StoryEntity>
}