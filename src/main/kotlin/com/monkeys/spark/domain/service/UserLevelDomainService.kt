package com.monkeys.spark.domain.service

/**
 * 사용자 레벨 계산 관련 순수 도메인 서비스
 */
class UserLevelDomainService {
    
    /**
     * 다음 레벨까지의 진행도 계산 (0-100%)
     */
    fun calculateProgressToNextLevel(currentPoints: Int, currentLevel: Int): Int {
        val pointsForCurrentLevel = calculatePointsForLevel(currentLevel)
        val pointsForNextLevel = calculatePointsForLevel(currentLevel + 1)
        val progressPoints = currentPoints - pointsForCurrentLevel
        val requiredPoints = pointsForNextLevel - pointsForCurrentLevel
        
        return if (requiredPoints > 0) {
            ((progressPoints.toDouble() / requiredPoints) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }
    
    /**
     * 다음 레벨까지 필요한 포인트 계산
     */
    fun calculatePointsToNextLevel(currentPoints: Int, currentLevel: Int): Int {
        val pointsForNextLevel = calculatePointsForLevel(currentLevel + 1)
        return (pointsForNextLevel - currentPoints).coerceAtLeast(0)
    }
    
    /**
     * 특정 레벨에 필요한 총 포인트 계산
     * 비즈니스 규칙: 각 레벨은 1000포인트씩 증가
     */
    private fun calculatePointsForLevel(level: Int): Int {
        return level * 1000
    }
    
    /**
     * 현재 포인트를 기반으로 적절한 레벨 계산
     */
    fun calculateLevelFromPoints(totalPoints: Int): Int {
        return (totalPoints / 1000).coerceAtLeast(1)
    }
}