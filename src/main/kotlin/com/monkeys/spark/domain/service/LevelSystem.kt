package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.vo.user.UserLevelTitle
import com.monkeys.spark.domain.vo.user.LevelInfo

/**
 * 레벨 시스템 관리 객체 (1-50 레벨)
 * 미션 보상: EASY(10-15), MEDIUM(20-25), HARD(30-35) 포인트 기준으로 설계
 */
object LevelSystem {
    
    /**
     * 레벨별 필요 포인트 정의
     * - 초반 (1-5): 3-5개 미션으로 레벨업 (50-100포인트 간격)
     * - 초중반 (6-10): 4-6개 미션으로 레벨업 (80-150포인트 간격)  
     * - 중반 (11-20): 6-10개 미션으로 레벨업 (150-250포인트 간격)
     * - 중후반 (21-30): 8-12개 미션으로 레벨업 (200-300포인트 간격)
     * - 후반 (31-40): 10-15개 미션으로 레벨업 (250-400포인트 간격)
     * - 최종 (41-50): 12-20개 미션으로 레벨업 (300-500포인트 간격)
     */
    private val levelPoints = mapOf(
        // BEGINNER 초보자 (1-5)
        1 to 0, 2 to 50, 3 to 120, 4 to 200, 5 to 300,
        
        // EXPLORER 탐험가 (6-10)  
        6 to 420, 7 to 560, 8 to 720, 9 to 900, 10 to 1100,
        
        // ADVENTURER 모험가 (11-20)
        11 to 1320, 12 to 1560, 13 to 1820, 14 to 2100, 15 to 2400,
        16 to 2720, 17 to 3060, 18 to 3420, 19 to 3800, 20 to 4200,
        
        // EXPERT 전문가 (21-30)
        21 to 4620, 22 to 5060, 23 to 5520, 24 to 6000, 25 to 6500,
        26 to 7020, 27 to 7560, 28 to 8120, 29 to 8700, 30 to 9300,
        
        // MASTER 마스터 (31-40)
        31 to 9920, 32 to 10560, 33 to 11220, 34 to 11900, 35 to 12600,
        36 to 13320, 37 to 14060, 38 to 14820, 39 to 15600, 40 to 16400,
        
        // GRANDMASTER 그랜드마스터 (41-45)
        41 to 17220, 42 to 18060, 43 to 18920, 44 to 19800, 45 to 20700,
        
        // LEGEND 전설 (46-50)
        46 to 21620, 47 to 22560, 48 to 23520, 49 to 24500, 50 to 25500
    )
    
    private fun generateLevels(): List<LevelInfo> {
        val levels = mutableListOf<LevelInfo>()
        
        for (level in 1..50) {
            val requiredPoints = levelPoints[level]!!
            val nextLevelPoints = levelPoints[level + 1]
            val (title, color, mainIcon) = when (level) {
                in 1..5 -> Triple(UserLevelTitle.BEGINNER, "#10B981", "🌱")
                in 6..10 -> Triple(UserLevelTitle.EXPLORER, "#3B82F6", "🔍")
                in 11..20 -> Triple(UserLevelTitle.ADVENTURER, "#F59E0B", "⚔️")
                in 21..30 -> Triple(UserLevelTitle.EXPERT, "#8B5CF6", "🎓")
                in 31..40 -> Triple(UserLevelTitle.MASTER, "#DC2626", "🏆")
                in 41..45 -> Triple(UserLevelTitle.GRANDMASTER, "#7C2D12", "👑")
                in 46..50 -> Triple(UserLevelTitle.LEGEND, "#7C3AED", "🚀")
                else -> Triple(UserLevelTitle.MYTHIC, "#1E1B4B", "✨")
            }
            
            val icon = getLevelIcon(level, mainIcon)
            val description = getLevelDescription(level, title)
            val benefits = getLevelBenefits(level)
            val badge = "${title.name.lowercase()}-badge"
            
            levels.add(
                LevelInfo(
                    level = level,
                    levelTitle = title,
                    requiredPoints = requiredPoints,
                    nextLevelPoints = nextLevelPoints,
                    description = description,
                    benefits = benefits,
                    icon = icon,
                    color = color,
                    badge = badge
                )
            )
        }
        
        return levels
    }
    
    private fun getLevelIcon(level: Int, mainIcon: String): String {
        return when (level) {
            // BEGINNER (1-5)
            1 -> "🌱"
            2 -> "🌿"
            3 -> "🍀"
            4 -> "🌾"
            5 -> "🌳"
            
            // EXPLORER (6-10)
            6 -> "🔍"
            7 -> "🧭"
            8 -> "🗺️"
            9 -> "🔬"
            10 -> "🌏"
            
            // ADVENTURER (11-20)  
            11 -> "⚔️"
            12 -> "🛡️"
            13 -> "🏔️"
            14 -> "🗻"
            15 -> "🎯"
            16 -> "🏹"
            17 -> "⛰️"
            18 -> "🌋"
            19 -> "🗡️"
            20 -> "🏰"
            
            // EXPERT (21-30)
            21 -> "🎓"
            22 -> "📚"
            23 -> "🔮"
            24 -> "💎"
            25 -> "🌟"
            26 -> "⭐"
            27 -> "🎖️"
            28 -> "🏅"
            29 -> "👑"
            30 -> "💫"
            
            // MASTER (31-40)
            31 -> "🏆"
            32 -> "🥇"
            33 -> "🔥"
            34 -> "⚡"
            35 -> "💥"
            36 -> "🌪️"
            37 -> "⚡"
            38 -> "🌊"
            39 -> "🌈"
            40 -> "✨"
            
            // GRANDMASTER (41-45)
            41 -> "👑"
            42 -> "💎"
            43 -> "🔱"
            44 -> "⚜️"
            45 -> "🌠"
            
            // LEGEND (46-50)
            46 -> "🚀"
            47 -> "🌌"
            48 -> "💥"
            49 -> "⭐"
            50 -> "🌟"
            
            else -> mainIcon
        }
    }
    
    private fun getLevelDescription(level: Int, title: UserLevelTitle): String {
        return when (level) {
            1 -> "미션 여행을 시작하는 첫 걸음"
            2 -> "미션에 익숙해지기 시작하는 단계"
            3 -> "꾸준한 도전으로 성장하는 단계"
            4 -> "미션의 재미를 느끼는 단계"
            5 -> "초보자에서 벗어나는 단계"
            
            6 -> "새로운 세계를 탐험하기 시작"
            7 -> "다양한 미션에 도전하는 단계"
            8 -> "탐험의 즐거움을 아는 단계"
            9 -> "깊이 있는 경험을 쌓는 단계"
            10 -> "탐험가로서 자리잡은 단계"
            
            11 -> "진정한 모험을 시작하는 단계"
            12 -> "어려운 도전을 즐기는 단계"
            13 -> "위험을 무릅쓰고 도전하는 단계"
            14 -> "극한을 추구하는 단계"
            15 -> "모험의 전문가가 되는 단계"
            16 -> "리더십을 발휘하는 단계"
            17 -> "팀을 이끄는 모험가"
            18 -> "전설의 시작을 알리는 단계"
            19 -> "숙련된 모험가의 경지"
            20 -> "모험가 중의 모험가"
            
            21 -> "전문가의 길에 들어서는 단계"
            22 -> "깊은 통찰력을 기르는 단계"
            23 -> "지혜와 경험이 쌓인 단계"
            24 -> "다른 이들의 멘토가 되는 단계"
            25 -> "전문성이 인정받는 단계"
            26 -> "지식과 실력을 겸비한 단계"
            27 -> "커뮤니티를 이끄는 전문가"
            28 -> "혁신을 추구하는 단계"
            29 -> "최고 수준의 전문성 보유"
            30 -> "전문가 중의 전문가"
            
            31 -> "마스터의 경지에 오른 단계"
            32 -> "완벽을 추구하는 단계"
            33 -> "예술적 경지에 도달한 단계"
            34 -> "전설적인 실력을 보유한 단계"
            35 -> "불가능을 가능으로 만드는 단계"
            36 -> "경이로운 성과를 이루는 단계"
            37 -> "영감을 주는 마스터"
            38 -> "시대를 이끄는 마스터"
            39 -> "역사에 남을 업적을 쌓는 단계"
            40 -> "진정한 마스터의 완성"
            
            41 -> "그랜드마스터의 위엄"
            42 -> "초월적 경지에 오른 단계"
            43 -> "신화적 존재가 되는 단계"
            44 -> "불멸의 명성을 얻은 단계"
            45 -> "그랜드마스터의 완성"
            
            46 -> "전설의 시작"
            47 -> "전설 중의 전설"
            48 -> "우주적 차원의 존재"
            49 -> "신화를 넘어선 존재"
            50 -> "영원한 전설"
            
            else -> "${title.displayName}의 경지에 있는 단계"
        }
    }
    
    private fun getLevelBenefits(level: Int): List<String> {
        return when (level) {
            1 -> listOf("기본 미션 접근", "프로필 생성")
            2 -> listOf("일일 미션 3개", "기본 리워드 접근")
            3 -> listOf("스토리 작성 기능", "좋아요 기능")
            4 -> listOf("댓글 작성 기능", "친구 추가")
            5 -> listOf("탐험 카테고리 해금", "주간 챌린지 참여")
            
            6 -> listOf("모든 기본 카테고리 해금", "그룹 활동 참여")
            7 -> listOf("특별 미션 접근", "리더보드 등록")
            8 -> listOf("창의 카테고리 미션 해금", "멘토 신청 가능")
            9 -> listOf("난이도 Medium 미션 해금", "커뮤니티 활동 확장")
            10 -> listOf("사회 카테고리 미션 해금", "팀 활동 리더 자격")
            
            11 -> listOf("모험 카테고리 미션 해금", "그룹 미션 생성")
            12 -> listOf("학습 카테고리 미션 해금", "멘토 역할 가능")
            13 -> listOf("난이도 Hard 미션 해금", "커스텀 미션 제안")
            14 -> listOf("특별 이벤트 우선 참여", "베타 기능 테스트")
            15 -> listOf("프리미엄 리워드 접근", "VIP 채널 접근")
            16 -> listOf("커뮤니티 모더레이터 자격", "특별 뱃지 획득")
            17 -> listOf("미션 큐레이션 참여", "개발팀 피드백 채널")
            18 -> listOf("전용 리워드 카테고리", "특별 할인 혜택")
            19 -> listOf("멘토링 시스템 접근", "플랫폼 운영 자문")
            20 -> listOf("VIP 이벤트 참여", "개발팀과 직접 소통")
            
            21 -> listOf("전문가 미션 접근", "컨텐츠 제작 참여")
            22 -> listOf("알파 기능 우선 테스트", "특별 인사이트 제공")
            23 -> listOf("커뮤니티 가이드 역할", "플랫폼 정책 자문")
            24 -> listOf("전문가 패널 참여", "신규 기능 기획 참여")
            25 -> listOf("독점 콘텐츠 접근", "브랜드 파트너십 기회")
            26 -> listOf("전문가 네트워킹", "컨퍼런스 초청")
            27 -> listOf("연구 프로젝트 참여", "학술 활동 지원")
            28 -> listOf("글로벌 커뮤니티 액세스", "국제 이벤트 참여")
            29 -> listOf("산업 리더와 네트워킹", "전문가 자격 인증")
            30 -> listOf("전문가 명예의 전당", "평생 특별 혜택")
            
            31 -> listOf("마스터 전용 미션", "플랫폼 운영 참여")
            32 -> listOf("마스터 클래스 주최", "후배 양성 프로그램")
            33 -> listOf("혁신 프로젝트 리더", "연구개발 참여")
            34 -> listOf("글로벌 마스터 네트워크", "국제 표준 제정 참여")
            35 -> listOf("산업 변화 선도", "차세대 기술 개발")
            36 -> listOf("역사적 프로젝트 참여", "레거시 구축")
            37 -> listOf("문명 발전 기여", "인류 지식 확장")
            38 -> listOf("시대적 혁신 주도", "패러다임 전환 선도")
            39 -> listOf("불멸의 업적 달성", "영원한 영향력")
            40 -> listOf("마스터의 완성", "모든 특권 영구 보장")
            
            41 -> listOf("그랜드마스터 권한", "최고 의사결정 참여")
            42 -> listOf("초월적 지혜 공유", "차원 간 소통")
            43 -> listOf("신화적 존재 인정", "전설 창조")
            44 -> listOf("불멸의 명성", "영원한 기록")
            45 -> listOf("그랜드마스터 완성", "우주적 영향력")
            
            46 -> listOf("전설의 시작", "신화 진입")
            47 -> listOf("전설적 존재", "역사 초월")
            48 -> listOf("우주적 차원", "시공간 초월")
            49 -> listOf("절대적 존재", "현실 조작")
            50 -> listOf("영원한 전설", "무한한 가능성", "창조의 근원")
            
            else -> listOf("특별한 혜택")
        }
    }
    
    private val levels by lazy { generateLevels() }
    
    /**
     * 레벨 번호로 레벨 정보 조회
     */
    fun getLevelInfo(level: Int): LevelInfo? {
        return levels.find { it.level == level }
            ?: if (level > 50) {
                // 50레벨 이상은 신화 등급으로 처리
                LevelInfo(
                    level = level,
                    levelTitle = UserLevelTitle.MYTHIC,
                    requiredPoints = calculatePointsForLevel(level),
                    nextLevelPoints = calculatePointsForLevel(level + 1),
                    description = "신화를 넘어선 절대적 존재",
                    benefits = listOf("모든 것을 초월한 권능", "창조와 파괴의 힘", "무한한 가능성"),
                    icon = "✨",
                    color = "#1E1B4B",
                    badge = "mythic-badge"
                )
            } else null
    }
    
    /**
     * 포인트로 레벨 계산 (새로운 1-50 레벨 시스템)
     */
    fun calculateLevelFromPoints(totalPoints: Int): Int {
        return levelPoints.entries
            .sortedByDescending { it.value }
            .find { totalPoints >= it.value }
            ?.key ?: 1
    }
    
    /**
     * 레벨별 필요 포인트 계산
     */
    fun calculatePointsForLevel(level: Int): Int {
        return levelPoints[level] ?: run {
            // 50레벨 초과 시 기하급수적 증가
            if (level > 50) {
                val basePoints = levelPoints[50]!!
                val additionalLevels = level - 50
                basePoints + (additionalLevels * 1000) // 레벨당 1000포인트씩 증가
            } else {
                0
            }
        }
    }
    
    /**
     * 다음 레벨까지 필요한 포인트 계산
     */
    fun getPointsToNextLevel(currentPoints: Int): Int {
        val currentLevel = calculateLevelFromPoints(currentPoints)
        if (currentLevel >= 50) {
            val nextLevelPoints = calculatePointsForLevel(currentLevel + 1)
            return maxOf(0, nextLevelPoints - currentPoints)
        }
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
        
        if (currentLevel >= 50 && nextLevelPoints <= currentLevelPoints) {
            return 100.0 // 최대 레벨 달성
        }
        
        val pointsInCurrentLevel = currentPoints - currentLevelPoints
        val pointsNeededForNextLevel = nextLevelPoints - currentLevelPoints
        
        return if (pointsNeededForNextLevel > 0) {
            (pointsInCurrentLevel.toDouble() / pointsNeededForNextLevel.toDouble() * 100).coerceIn(0.0, 100.0)
        } else {
            100.0
        }
    }
    
    /**
     * 모든 레벨 정보 조회 (1-50 레벨)
     */
    fun getAllLevels(): List<LevelInfo> = levels
    
    /**
     * 레벨 타이틀별 레벨 범위 조회
     */
    fun getLevelsByTitle(levelTitle: UserLevelTitle): List<LevelInfo> {
        return levels.filter { it.levelTitle == levelTitle }
    }
}