package com.monkeys.spark.adapter.outbound.persistence

import com.monkeys.spark.application.port.out.UserStatsRepository
import com.monkeys.spark.domain.model.UserStats
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.port.outbound.UserStatsPort
import org.springframework.stereotype.Component

/**
 * 사용자 스탯 포트 어댑터
 * 업적 시스템에서 사용자 스탯 정보 조회를 위한 어댑터
 */
@Component
class UserStatsPortAdapter(
    private val userStatsRepository: UserStatsRepository
) : UserStatsPort {
    
    override fun findByUserId(userId: String): UserStats? {
        return userStatsRepository.findByUserId(UserId(userId))
    }
}