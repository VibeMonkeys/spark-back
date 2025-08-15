package com.monkeys.spark.port.outbound

import com.monkeys.spark.domain.model.UserStats

/**
 * 사용자 스탯 데이터 접근을 위한 포트 인터페이스
 * 업적 시스템에서 사용자 스탯 정보를 조회하기 위함
 */
interface UserStatsPort {
    
    /**
     * 사용자 ID로 스탯 정보 조회
     */
    fun findByUserId(userId: String): UserStats?
}