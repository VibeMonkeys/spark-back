package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.common.Points
import com.monkeys.spark.domain.vo.user.Email
import com.monkeys.spark.domain.vo.user.Level
import com.monkeys.spark.domain.vo.mission.MissionCategory
import java.time.LocalDateTime

interface UserRepository {
    
    /**
     * 사용자 저장 (생성 및 수정)
     */
    fun save(user: User): User
    
    /**
     * 비밀번호와 함께 사용자 저장 (생성 시 사용)
     */
    fun saveWithPassword(user: User, password: String): User
    
    /**
     * 사용자 ID로 조회
     */
    fun findById(userId: UserId): User?
    
    /**
     * 이메일로 사용자 조회
     */
    fun findByEmail(email: Email): User?
    
    /**
     * 사용자 존재 여부 확인
     */
    fun existsById(userId: UserId): Boolean
    
    /**
     * 이메일 존재 여부 확인
     */
    fun existsByEmail(email: Email): Boolean
    
    /**
     * 사용자 삭제
     */
    fun deleteById(userId: UserId)
    
    /**
     * 모든 사용자 조회 (페이징)
     */
    fun findAll(page: Int, size: Int): List<User>
    
    /**
     * 레벨별 사용자 조회
     */
    fun findByLevel(level: Level): List<User>
    
    /**
     * 특정 포인트 이상 사용자 조회
     */
    fun findByCurrentPointsGreaterThan(points: Points): List<User>
    
    /**
     * 연속 수행일 기준 상위 사용자 조회
     */
    fun findTopUsersByStreak(limit: Int): List<User>
    
    /**
     * 이번 달 포인트 기준 상위 사용자 조회
     */
    fun findTopUsersByThisMonthPoints(limit: Int): List<User>
    
    /**
     * 특정 기간 동안 가입한 사용자 수
     */
    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long
    
    /**
     * 사용자 선호도 업데이트
     */
    fun updatePreferences(userId: UserId, preferences: Map<MissionCategory, Boolean>): User?
    
    /**
     * 사용자 통계 업데이트
     */
    fun updateStatistics(userId: UserId, categoryStats: Map<MissionCategory, Int>): User?
    
    /**
     * 비활성 사용자 조회 (특정 기간 동안 접속하지 않은 사용자)
     */
    fun findInactiveUsers(lastLoginBefore: LocalDateTime): List<User>
}