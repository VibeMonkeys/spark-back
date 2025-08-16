package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.vo.user.UserLevelTitle
import com.monkeys.spark.domain.vo.user.LevelInfo

/**
 * 레벨 시스템 관리 객체
 */
object LevelSystem {
    
    private val levels = listOf(
        LevelInfo(
            level = 1,
            levelTitle = UserLevelTitle.BEGINNER,
            requiredPoints = 0,
            nextLevelPoints = 500,
            description = "미션 여행을 시작하는 단계입니다",
            benefits = listOf("기본 미션 접근", "프로필 생성", "포인트 적립 시작"),
            icon = "🌱",
            color = "#10B981",
            badge = "beginner-badge"
        ),
        LevelInfo(
            level = 2,
            levelTitle = UserLevelTitle.BEGINNER,
            requiredPoints = 500,
            nextLevelPoints = 1500,
            description = "미션에 익숙해지기 시작하는 단계입니다",
            benefits = listOf("일일 미션 3개", "기본 리워드 접근"),
            icon = "🌿",
            color = "#10B981",
            badge = "beginner-badge"
        ),
        LevelInfo(
            level = 3,
            levelTitle = UserLevelTitle.EXPLORER,
            requiredPoints = 1500,
            nextLevelPoints = 3000,
            description = "새로운 경험을 탐험하기 시작합니다",
            benefits = listOf("탐험 카테고리 미션 해금", "주간 챌린지 참여"),
            icon = "🔍",
            color = "#3B82F6",
            badge = "explorer-badge"
        ),
        LevelInfo(
            level = 4,
            levelTitle = UserLevelTitle.EXPLORER,
            requiredPoints = 3000,
            nextLevelPoints = 5000,
            description = "다양한 미션에 도전하는 단계입니다",
            benefits = listOf("친구 추가 기능", "스토리 공유 확장"),
            icon = "🧭",
            color = "#3B82F6",
            badge = "explorer-badge"
        ),
        LevelInfo(
            level = 5,
            levelTitle = UserLevelTitle.EXPLORER,
            requiredPoints = 5000,
            nextLevelPoints = 8000,
            description = "탐험가로서 자리잡은 단계입니다",
            benefits = listOf("특별 미션 접근", "리더보드 등록"),
            icon = "🗺️",
            color = "#3B82F6",
            badge = "explorer-badge"
        ),
        LevelInfo(
            level = 6,
            levelTitle = UserLevelTitle.ADVENTURER,
            requiredPoints = 8000,
            nextLevelPoints = 12000,
            description = "진정한 모험을 시작하는 단계입니다",
            benefits = listOf("모험 카테고리 미션 해금", "그룹 미션 참여"),
            icon = "⚔️",
            color = "#F59E0B",
            badge = "adventurer-badge"
        ),
        LevelInfo(
            level = 7,
            levelTitle = UserLevelTitle.ADVENTURER,
            requiredPoints = 12000,
            nextLevelPoints = 17000,
            description = "위험을 무릅쓰고 도전하는 단계입니다",
            benefits = listOf("난이도 Hard 미션 해금", "멘토 역할 가능"),
            icon = "🏔️",
            color = "#F59E0B",
            badge = "adventurer-badge"
        ),
        LevelInfo(
            level = 8,
            levelTitle = UserLevelTitle.ADVENTURER,
            requiredPoints = 17000,
            nextLevelPoints = 23000,
            description = "숙련된 모험가로 성장한 단계입니다",
            benefits = listOf("커스텀 미션 생성", "팀 리더 자격"),
            icon = "🎯",
            color = "#F59E0B",
            badge = "adventurer-badge"
        ),
        LevelInfo(
            level = 9,
            levelTitle = UserLevelTitle.EXPERT,
            requiredPoints = 23000,
            nextLevelPoints = 30000,
            description = "전문가 수준의 경험을 쌓은 단계입니다",
            benefits = listOf("전문가 미션 해금", "컨텐츠 큐레이션 참여"),
            icon = "🎓",
            color = "#8B5CF6",
            badge = "expert-badge"
        ),
        LevelInfo(
            level = 10,
            levelTitle = UserLevelTitle.EXPERT,
            requiredPoints = 30000,
            nextLevelPoints = 38000,
            description = "깊은 통찰력을 가진 전문가입니다",
            benefits = listOf("베타 기능 우선 접근", "커뮤니티 모더레이터"),
            icon = "💎",
            color = "#8B5CF6",
            badge = "expert-badge"
        ),
        LevelInfo(
            level = 11,
            levelTitle = UserLevelTitle.EXPERT,
            requiredPoints = 38000,
            nextLevelPoints = 47000,
            description = "다른 사용자들의 멘토가 되는 단계입니다",
            benefits = listOf("멘토링 시스템 접근", "전용 리워드 카테고리"),
            icon = "🌟",
            color = "#8B5CF6",
            badge = "expert-badge"
        ),
        LevelInfo(
            level = 12,
            levelTitle = UserLevelTitle.EXPERT,
            requiredPoints = 47000,
            nextLevelPoints = 57000,
            description = "최고 수준의 전문성을 보유한 단계입니다",
            benefits = listOf("VIP 이벤트 참여", "개발팀과의 직접 소통"),
            icon = "👑",
            color = "#8B5CF6",
            badge = "expert-badge"
        ),
        LevelInfo(
            level = 13,
            levelTitle = UserLevelTitle.MASTER,
            requiredPoints = 57000,
            nextLevelPoints = 69000,
            description = "진정한 마스터로 인정받는 단계입니다",
            benefits = listOf("마스터 전용 미션", "플랫폼 운영 참여"),
            icon = "🏆",
            color = "#DC2626",
            badge = "master-badge"
        ),
        LevelInfo(
            level = 20,
            levelTitle = UserLevelTitle.MASTER,
            requiredPoints = 141000,
            nextLevelPoints = 153000,
            description = "최고 레벨의 마스터입니다",
            benefits = listOf("모든 기능 접근", "레거시 사용자 특전"),
            icon = "⭐",
            color = "#DC2626",
            badge = "master-badge"
        ),
        LevelInfo(
            level = 21,
            levelTitle = UserLevelTitle.LEGEND,
            requiredPoints = 153000,
            nextLevelPoints = null,
            description = "전설적인 사용자로 남을 단계입니다",
            benefits = listOf("레전드 명예", "영구 특별 혜택", "플랫폼 기여자 명예"),
            icon = "🚀",
            color = "#7C3AED",
            badge = "legend-badge"
        )
    )
    
    /**
     * 레벨 번호로 레벨 정보 조회
     */
    fun getLevelInfo(level: Int): LevelInfo? {
        return levels.find { it.level == level }
            ?: if (level > 21) {
                // 21레벨 이상은 레전드로 처리
                levels.last().copy(
                    level = level,
                    requiredPoints = calculatePointsForLevel(level),
                    nextLevelPoints = calculatePointsForLevel(level + 1),
                    description = "전설을 넘어선 단계입니다"
                )
            } else null
    }
    
    /**
     * 포인트로 레벨 계산 (User 모델의 로직과 동일)
     */
    fun calculateLevelFromPoints(totalPoints: Int): Int {
        return when {
            totalPoints < 500 -> 1
            totalPoints < 1500 -> 2
            totalPoints < 3000 -> 3
            totalPoints < 5000 -> 4
            totalPoints < 8000 -> 5
            totalPoints < 12000 -> 6
            totalPoints < 17000 -> 7
            totalPoints < 23000 -> 8
            totalPoints < 30000 -> 9
            totalPoints < 38000 -> 10
            totalPoints < 47000 -> 11
            totalPoints < 57000 -> 12
            else -> ((totalPoints - 57000) / 12000) + 13
        }
    }
    
    /**
     * 레벨별 필요 포인트 계산
     */
    fun calculatePointsForLevel(level: Int): Int {
        return when (level) {
            1 -> 0
            2 -> 500
            3 -> 1500
            4 -> 3000
            5 -> 5000
            6 -> 8000
            7 -> 12000
            8 -> 17000
            9 -> 23000
            10 -> 30000
            11 -> 38000
            12 -> 47000
            else -> 57000 + (level - 13) * 12000
        }
    }
    
    /**
     * 다음 레벨까지 필요한 포인트 계산
     */
    fun getPointsToNextLevel(currentPoints: Int): Int {
        val currentLevel = calculateLevelFromPoints(currentPoints)
        val nextLevelPoints = calculatePointsForLevel(currentLevel + 1)
        return maxOf(0, nextLevelPoints - currentPoints)
    }
    
    /**
     * 현재 레벨에서의 진행률 계산 (0-100)
     */
    fun getLevelProgress(currentPoints: Int): Double {
        val currentLevel = calculateLevelFromPoints(currentPoints)
        val currentLevelPoints = calculatePointsForLevel(currentLevel)
        val nextLevelPoints = calculatePointsForLevel(currentLevel + 1)
        
        val pointsInCurrentLevel = currentPoints - currentLevelPoints
        val pointsNeededForNextLevel = nextLevelPoints - currentLevelPoints
        
        return (pointsInCurrentLevel.toDouble() / pointsNeededForNextLevel.toDouble() * 100).coerceIn(0.0, 100.0)
    }
    
    /**
     * 모든 레벨 정보 조회
     */
    fun getAllLevels(): List<LevelInfo> = levels
    
    /**
     * 레벨 타이틀별 레벨 범위 조회
     */
    fun getLevelsByTitle(levelTitle: UserLevelTitle): List<LevelInfo> {
        return levels.filter { it.levelTitle == levelTitle }
    }
}