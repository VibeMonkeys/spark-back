package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.domain.vo.user.LevelInfo
import com.monkeys.spark.domain.model.User

interface LevelUseCase {
    
    /**
     * 사용자의 레벨 진행 상황 조회
     */
    fun getUserLevelProgress(userId: String): User
    
    /**
     * 전체 레벨 시스템 정보 조회
     */
    fun getLevelSystem(userId: String): User
    
    /**
     * 특정 레벨 정보 조회
     */
    fun getLevelInfo(level: Int): LevelInfo?
    
    /**
     * 모든 레벨 정보 조회
     */
    fun getAllLevels(): List<LevelInfo>
}