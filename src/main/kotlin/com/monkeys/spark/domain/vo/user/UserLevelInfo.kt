package com.monkeys.spark.domain.vo.user

import com.monkeys.spark.domain.vo.common.Points

/**
 * 사용자의 현재 레벨 상태를 나타내는 Value Object
 * 레벨 계산 로직을 캡슐화하여 애그리게이트의 순수성을 유지
 */
data class UserLevelInfo(
    val level: Level,
    val levelTitle: UserLevelTitle,
    val pointsToNext: Int,
    val progressPercentage: Double
) {
    companion object {
        /**
         * 총 포인트를 기반으로 레벨 정보를 계산
         * LevelSystem의 복잡한 포인트 테이블을 사용
         */
        fun fromPoints(totalPoints: Points): UserLevelInfo {
            val pointsValue = totalPoints.value
            val levelValue = calculateLevelFromPoints(pointsValue)
            val levelTitle = calculateLevelTitle(levelValue)
            val pointsToNext = calculatePointsToNextLevel(pointsValue, levelValue)
            val progressPercentage = calculateLevelProgress(pointsValue, levelValue)
            
            return UserLevelInfo(
                level = Level(levelValue),
                levelTitle = levelTitle,
                pointsToNext = pointsToNext,
                progressPercentage = progressPercentage
            )
        }
        
        /**
         * 포인트로 레벨 계산 (50레벨 시스템)
         */
        private fun calculateLevelFromPoints(totalPoints: Int): Int {
            val levelPoints = mapOf(
                1 to 0, 2 to 50, 3 to 120, 4 to 200, 5 to 300,
                6 to 420, 7 to 560, 8 to 720, 9 to 900, 10 to 1100,
                11 to 1320, 12 to 1560, 13 to 1820, 14 to 2100, 15 to 2400,
                16 to 2720, 17 to 3060, 18 to 3420, 19 to 3800, 20 to 4200,
                21 to 4620, 22 to 5060, 23 to 5520, 24 to 6000, 25 to 6500,
                26 to 7020, 27 to 7560, 28 to 8120, 29 to 8700, 30 to 9300,
                31 to 9920, 32 to 10560, 33 to 11220, 34 to 11900, 35 to 12600,
                36 to 13320, 37 to 14060, 38 to 14820, 39 to 15600, 40 to 16400,
                41 to 17220, 42 to 18060, 43 to 18920, 44 to 19800, 45 to 20700,
                46 to 21620, 47 to 22560, 48 to 23520, 49 to 24500, 50 to 25500
            )
            
            return levelPoints.entries
                .sortedByDescending { it.value }
                .find { totalPoints >= it.value }
                ?.key ?: 1
        }
        
        /**
         * 레벨 타이틀 계산
         */
        private fun calculateLevelTitle(level: Int): UserLevelTitle {
            return when (level) {
                in 1..5 -> UserLevelTitle.BEGINNER
                in 6..10 -> UserLevelTitle.EXPLORER
                in 11..20 -> UserLevelTitle.ADVENTURER
                in 21..30 -> UserLevelTitle.EXPERT
                in 31..40 -> UserLevelTitle.MASTER
                in 41..45 -> UserLevelTitle.GRANDMASTER
                in 46..50 -> UserLevelTitle.LEGEND
                else -> UserLevelTitle.MYTHIC
            }
        }
        
        /**
         * 다음 레벨까지 필요한 포인트 계산
         */
        private fun calculatePointsToNextLevel(currentPoints: Int, currentLevel: Int): Int {
            val levelPoints = mapOf(
                1 to 0, 2 to 50, 3 to 120, 4 to 200, 5 to 300,
                6 to 420, 7 to 560, 8 to 720, 9 to 900, 10 to 1100,
                11 to 1320, 12 to 1560, 13 to 1820, 14 to 2100, 15 to 2400,
                16 to 2720, 17 to 3060, 18 to 3420, 19 to 3800, 20 to 4200,
                21 to 4620, 22 to 5060, 23 to 5520, 24 to 6000, 25 to 6500,
                26 to 7020, 27 to 7560, 28 to 8120, 29 to 8700, 30 to 9300,
                31 to 9920, 32 to 10560, 33 to 11220, 34 to 11900, 35 to 12600,
                36 to 13320, 37 to 14060, 38 to 14820, 39 to 15600, 40 to 16400,
                41 to 17220, 42 to 18060, 43 to 18920, 44 to 19800, 45 to 20700,
                46 to 21620, 47 to 22560, 48 to 23520, 49 to 24500, 50 to 25500
            )
            
            if (currentLevel >= 50) {
                return 0 // 최대 레벨 달성
            }
            
            val nextLevelPoints = levelPoints[currentLevel + 1] ?: return 0
            return maxOf(0, nextLevelPoints - currentPoints)
        }
        
        /**
         * 현재 레벨에서의 진행률 계산 (0-100)
         */
        private fun calculateLevelProgress(currentPoints: Int, currentLevel: Int): Double {
            val levelPoints = mapOf(
                1 to 0, 2 to 50, 3 to 120, 4 to 200, 5 to 300,
                6 to 420, 7 to 560, 8 to 720, 9 to 900, 10 to 1100,
                11 to 1320, 12 to 1560, 13 to 1820, 14 to 2100, 15 to 2400,
                16 to 2720, 17 to 3060, 18 to 3420, 19 to 3800, 20 to 4200,
                21 to 4620, 22 to 5060, 23 to 5520, 24 to 6000, 25 to 6500,
                26 to 7020, 27 to 7560, 28 to 8120, 29 to 8700, 30 to 9300,
                31 to 9920, 32 to 10560, 33 to 11220, 34 to 11900, 35 to 12600,
                36 to 13320, 37 to 14060, 38 to 14820, 39 to 15600, 40 to 16400,
                41 to 17220, 42 to 18060, 43 to 18920, 44 to 19800, 45 to 20700,
                46 to 21620, 47 to 22560, 48 to 23520, 49 to 24500, 50 to 25500
            )
            
            if (currentLevel >= 50) {
                return 100.0 // 최대 레벨 달성
            }
            
            val currentLevelPoints = levelPoints[currentLevel] ?: 0
            val nextLevelPoints = levelPoints[currentLevel + 1] ?: return 100.0
            
            val pointsInCurrentLevel = currentPoints - currentLevelPoints
            val pointsNeededForNextLevel = nextLevelPoints - currentLevelPoints
            
            return if (pointsNeededForNextLevel > 0) {
                (pointsInCurrentLevel.toDouble() / pointsNeededForNextLevel.toDouble() * 100).coerceIn(0.0, 100.0)
            } else {
                100.0
            }
        }
    }
    
    /**
     * 레벨업이 발생했는지 확인
     */
    fun isLevelUp(previousLevel: Level): Boolean {
        return level.value > previousLevel.value
    }
}