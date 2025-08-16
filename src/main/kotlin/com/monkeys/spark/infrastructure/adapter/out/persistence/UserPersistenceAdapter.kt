package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.UserRepository
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.MissionCategory
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.UserEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.UserJpaRepository
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
    private val userMapper: UserPersistenceMapper,
    private val passwordEncoder: PasswordEncoder
) : UserRepository {

    override fun save(user: User): User {
        val entity = userMapper.toEntity(user)
        val savedEntity = userJpaRepository.save(entity)
        return userMapper.toDomain(savedEntity)
    }

    override fun saveWithPassword(user: User, password: String): User {
        // User 도메인 모델에 해시된 비밀번호 설정
        user.password = passwordEncoder.encode(password)
        val entity = userMapper.toEntity(user)
        val savedEntity = userJpaRepository.save(entity)
        return userMapper.toDomain(savedEntity)
    }

    override fun findById(id: UserId): User? {
        return userJpaRepository.findById(id.value)
            .map { userMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByEmail(email: Email): User? {
        return userJpaRepository.findByEmail(email.value)
            ?.let { userMapper.toDomain(it) }
    }

    override fun existsById(userId: UserId): Boolean {
        return userJpaRepository.existsById(userId.value)
    }

    override fun existsByEmail(email: Email): Boolean {
        return userJpaRepository.findByEmail(email.value) != null
    }

    override fun deleteById(userId: UserId) {
        userJpaRepository.deleteById(userId.value)
    }

    override fun findAll(page: Int, size: Int): List<User> {
        val pageable = PageRequest.of(page, size)
        return userJpaRepository.findAll(pageable)
            .content
            .map { userMapper.toDomain(it) }
    }

    override fun findByLevel(level: Level): List<User> {
        return userJpaRepository.findByLevel(level.value)
            .map { userMapper.toDomain(it) }
    }

    override fun findByCurrentPointsGreaterThan(points: Points): List<User> {
        return userJpaRepository.findByCurrentPointsGreaterThan(points.value)
            .map { userMapper.toDomain(it) }
    }

    override fun findTopUsersByStreak(limit: Int): List<User> {
        val pageable = PageRequest.of(0, limit)
        return userJpaRepository.findAllByOrderByCurrentStreakDesc(pageable)
            .content
            .map { userMapper.toDomain(it) }
    }

    override fun findTopUsersByThisMonthPoints(limit: Int): List<User> {
        val pageable = PageRequest.of(0, limit)
        // TODO: 이번 달 포인트를 별도로 계산하는 로직이 필요
        // 임시로 전체 포인트 기준으로 정렬
        return userJpaRepository.findAllByOrderByTotalPointsDesc(pageable)
            .content
            .map { userMapper.toDomain(it) }
    }

    override fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long {
        return userJpaRepository.countByCreatedAtBetween(startDate, endDate)
    }

    override fun updatePreferences(userId: UserId, preferences: Map<MissionCategory, Boolean>): User? {
        return userJpaRepository.findById(userId.value).map { entity ->
            // JSON 형태로 선호도 저장
            val preferencesJson = preferences.entries.joinToString(",") { "\"${it.key.name}\":${it.value}" }
            entity.preferences = "{$preferencesJson}"
            entity.updatedAt = LocalDateTime.now()
            userJpaRepository.save(entity)
            userMapper.toDomain(entity)
        }.orElse(null)
    }

    override fun updateStatistics(userId: UserId, categoryStats: Map<MissionCategory, Int>): User? {
        return userJpaRepository.findById(userId.value).map { entity ->
            // JSON 형태로 통계 저장
            val statsJson = categoryStats.entries.joinToString(",") { "\"${it.key.name}\":${it.value}" }
            entity.categoryStats = "{$statsJson}"
            entity.updatedAt = LocalDateTime.now()
            userJpaRepository.save(entity)
            userMapper.toDomain(entity)
        }.orElse(null)
    }

    override fun findInactiveUsers(lastLoginBefore: LocalDateTime): List<User> {
        return userJpaRepository.findByLastLoginAtBefore(lastLoginBefore)
            .map { userMapper.toDomain(it) }
    }
}