package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 사용자 스탯 JPA 엔티티
 */
@Entity
@Table(name = "user_stats")
data class UserStatsEntity(
    @Id
    @Column(name = "user_id")
    val userId: Long,

    // 힘 스탯
    @Column(name = "strength_current", nullable = false)
    val strengthCurrent: Int = 10,

    @Column(name = "strength_allocated", nullable = false)
    val strengthAllocated: Int = 0,

    // 지능 스탯
    @Column(name = "intelligence_current", nullable = false)
    val intelligenceCurrent: Int = 10,

    @Column(name = "intelligence_allocated", nullable = false)
    val intelligenceAllocated: Int = 0,

    // 창의력 스탯
    @Column(name = "creativity_current", nullable = false)
    val creativityCurrent: Int = 10,

    @Column(name = "creativity_allocated", nullable = false)
    val creativityAllocated: Int = 0,

    // 사교성 스탯
    @Column(name = "sociability_current", nullable = false)
    val sociabilityCurrent: Int = 10,

    @Column(name = "sociability_allocated", nullable = false)
    val sociabilityAllocated: Int = 0,

    // 모험심 스탯
    @Column(name = "adventurous_current", nullable = false)
    val adventurousCurrent: Int = 10,

    @Column(name = "adventurous_allocated", nullable = false)
    val adventurousAllocated: Int = 0,

    // 규율성 스탯
    @Column(name = "discipline_current", nullable = false)
    val disciplineCurrent: Int = 10,

    @Column(name = "discipline_allocated", nullable = false)
    val disciplineAllocated: Int = 0,

    // 포인트 관리
    @Column(name = "available_points", nullable = false)
    val availablePoints: Int = 0,

    @Column(name = "total_earned_points", nullable = false)
    val totalEarnedPoints: Int = 0,

    // 시간 정보
    @Column(name = "last_updated_at", nullable = false)
    val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    // JPA를 위한 기본 생성자
    constructor() : this(
        userId = 0L,
        strengthCurrent = 10,
        strengthAllocated = 0,
        intelligenceCurrent = 10,
        intelligenceAllocated = 0,
        creativityCurrent = 10,
        creativityAllocated = 0,
        sociabilityCurrent = 10,
        sociabilityAllocated = 0,
        adventurousCurrent = 10,
        adventurousAllocated = 0,
        disciplineCurrent = 10,
        disciplineAllocated = 0,
        availablePoints = 0,
        totalEarnedPoints = 0,
        lastUpdatedAt = LocalDateTime.now(),
        createdAt = LocalDateTime.now()
    )

}