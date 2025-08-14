package com.monkeys.spark.domain.factory

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.MissionConditions
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*
import org.springframework.stereotype.Component

@Component
class MissionFactory {

    /**
     * 사용자별 일일 미션 3개 생성
     */
    fun createDailyMissions(user: User): List<Mission> {
        val missionTemplates = getAllMissionTemplates()

        // 사용자 선호도 기반으로 미션 필터링
        val availableCategories = user.preferences.filter { it.value }.keys.toList()
        val filteredTemplates = if (availableCategories.isNotEmpty()) {
            missionTemplates.filter { it.category in availableCategories }
        } else {
            missionTemplates
        }

        // 난이도별로 1개씩 선택
        val easyMission = filteredTemplates.filter { it.difficulty == MissionDifficulty.EASY }.randomOrNull()
        val mediumMission = filteredTemplates.filter { it.difficulty == MissionDifficulty.MEDIUM }.randomOrNull()
        val hardMission = filteredTemplates.filter { it.difficulty == MissionDifficulty.HARD }.randomOrNull()

        val selectedMissions = listOfNotNull(easyMission, mediumMission, hardMission)

        // 3개가 안되면 남은 템플릿에서 랜덤 선택
        val missions = if (selectedMissions.size < 3) {
            val remaining = filteredTemplates - selectedMissions.toSet()
            selectedMissions + remaining.shuffled().take(3 - selectedMissions.size)
        } else {
            selectedMissions
        }

        // 사용자별 미션으로 변환
        return missions.take(3).map { template ->
            createPersonalizedMission(user, template)
        }
    }

    /**
     * 개인화된 미션 생성
     */
    private fun createPersonalizedMission(user: User, template: MissionTemplate): Mission {
        return Mission.create(
            userId = user.id,
            title = template.title,
            description = template.description,
            detailedDescription = template.detailedDescription,
            category = template.category,
            difficulty = template.difficulty,
            imageUrl = template.imageUrl,
            tips = template.tips,
            conditions = generateConditions(template.category)
        )
    }

    /**
     * 카테고리에 따른 조건 생성
     */
    private fun generateConditions(category: MissionCategory): MissionConditions {
        return when (category) {
            MissionCategory.SOCIAL -> MissionConditions(
                timeOfDay = "낮시간",
                location = Location("카페나 공공장소"),
                safetyLevel = "안전"
            )

            MissionCategory.ADVENTURE -> MissionConditions(
                timeOfDay = "모든 시간",
                safetyLevel = "주의 필요"
            )

            MissionCategory.HEALTH -> MissionConditions(
                timeOfDay = "아침이나 저녁",
                equipmentRequired = mutableListOf("편안한 신발"),
                safetyLevel = "안전"
            )

            MissionCategory.CREATIVE -> MissionConditions(
                timeOfDay = "모든 시간",
                location = Location("조용한 공간"),
                equipmentRequired = mutableListOf("창작 도구")
            )

            MissionCategory.LEARNING -> MissionConditions(
                timeOfDay = "집중 가능한 시간",
                location = Location("조용한 공간"),
                equipmentRequired = mutableListOf("학습 자료")
            )
        }
    }

    /**
     * 모든 미션 템플릿 반환
     */
    private fun getAllMissionTemplates(): List<MissionTemplate> {
        return listOf(
            // 사교적 미션들
            MissionTemplate(
                category = MissionCategory.SOCIAL,
                difficulty = MissionDifficulty.EASY,
                title = "카페에서 직원과 인사하기",
                description = "카페에서 주문할 때 직원분께 밝게 인사해보세요",
                detailedDescription = "평소보다 조금 더 밝은 목소리로 '안녕하세요'라고 인사하고, 주문 후 '감사합니다'를 꼭 말해보세요. 작은 인사가 하루를 따뜻하게 만들어줍니다.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "자연스럽고 진심 어린 인사를 해보세요",
                    "상대방의 눈을 보며 말하면 더 좋아요",
                    "미소를 짓는 것도 잊지 마세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.SOCIAL,
                difficulty = MissionDifficulty.MEDIUM,
                title = "카페에서 옆 테이블 사람과 대화하기",
                description = "자연스럽게 인사하며 짧은 대화를 나눠보세요",
                detailedDescription = "카페에서 혼자 앉아있는 분께 자연스럽게 다가가 간단한 인사나 날씨 얘기부터 시작해보세요. 강요하지 말고 상대방이 편안해 보일 때만 대화를 이어가세요.",
                imageUrl = "https://images.unsplash.com/photo-1655579932488-e05b9f649ede",
                tips = listOf(
                    "날씨나 카페 분위기 같은 자연스러운 주제로 시작하세요",
                    "상대방이 불편해하면 바로 물러나세요",
                    "진심 어린 관심을 보여주세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.SOCIAL,
                difficulty = MissionDifficulty.HARD,
                title = "모르는 사람과 프리스비 하기",
                description = "공원에서 모르는 사람과 함께 프리스비를 해보세요",
                detailedDescription = "한강공원이나 넓은 공원에서 프리스비나 공을 가져가 혼자 던지고 받기를 하다가, 지나가는 사람들에게 함께 할 것을 제안해보세요.",
                imageUrl = "https://images.unsplash.com/photo-1584515501397-335d595b2a17",
                tips = listOf(
                    "안전한 공원에서 진행하세요",
                    "거절당해도 웃으며 넘어가세요",
                    "먼저 혼자 하는 모습을 보여주면 더 자연스러워요"
                )
            ),

            // 모험적 미션들
            MissionTemplate(
                category = MissionCategory.ADVENTURE,
                difficulty = MissionDifficulty.EASY,
                title = "버스 대신 한 정거장 걸어가기",
                description = "걸으면서 동네를 새롭게 관찰해보세요",
                detailedDescription = "평소 버스나 지하철을 타고 가던 곳 중 한 정거장을 걸어서 가보세요. 천천히 걸으며 평소에 놓쳤던 가게들, 건물들, 사람들을 관찰해보세요.",
                imageUrl = "https://images.unsplash.com/photo-1584515501397-335d595b2a17",
                tips = listOf(
                    "시간 여유가 있을 때 진행하세요",
                    "새로 발견한 것들을 사진으로 기록해보세요",
                    "안전한 시간대와 경로를 선택하세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.ADVENTURE,
                difficulty = MissionDifficulty.MEDIUM,
                title = "가보지 않은 길로 퇴근하기",
                description = "평소와 다른 길을 선택해서 새로운 풍경을 만나보세요",
                detailedDescription = "오늘은 평소 다니던 길 대신 새로운 경로를 선택해보세요. 지도를 보지 말고 직감을 따라 걸어보거나, 평소에 지나치기만 했던 골목길로 들어가 보세요.",
                imageUrl = "https://images.unsplash.com/photo-1584515501397-335d595b2a17",
                tips = listOf(
                    "안전한 시간대와 장소를 선택하세요",
                    "휴대폰 배터리와 교통카드를 확인하세요",
                    "발견한 것들을 사진으로 기록해보세요",
                    "시간에 여유를 두고 출발하세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.ADVENTURE,
                difficulty = MissionDifficulty.HARD,
                title = "새로운 동네 탐험하기",
                description = "가본 적 없는 동네에서 2시간 보내기",
                detailedDescription = "지하철이나 버스를 타고 가본 적 없는 동네로 가보세요. 최소 2시간 동안 그 동네를 걸어다니며 카페, 맛집, 상점들을 둘러보고 그 동네만의 특색을 찾아보세요.",
                imageUrl = "https://images.unsplash.com/photo-1584515501397-335d595b2a17",
                tips = listOf(
                    "대중교통으로 접근 가능한 곳을 선택하세요",
                    "동네 맛집이나 카페를 미리 찾아보세요",
                    "현금을 준비해가세요",
                    "편한 신발을 착용하세요"
                )
            ),

            // 건강 미션들
            MissionTemplate(
                category = MissionCategory.HEALTH,
                difficulty = MissionDifficulty.EASY,
                title = "계단으로 5층 올라가기",
                description = "엘리베이터 대신 계단을 이용해 건강한 하루를 시작하세요",
                detailedDescription = "오늘 엘리베이터를 타지 말고 계단으로 5층까지 올라가보세요. 천천히, 자신의 페이스에 맞춰 올라가며 몸의 변화를 느껴보세요.",
                imageUrl = "https://images.unsplash.com/photo-1597644568217-780bd0b0efb2",
                tips = listOf(
                    "무릎이 아프면 중간에 쉬어가세요",
                    "물을 미리 마시고 시작하세요",
                    "편한 신발을 신고 하세요",
                    "호흡을 잊지 마세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.HEALTH,
                difficulty = MissionDifficulty.MEDIUM,
                title = "30분 동안 산책하기",
                description = "근처 공원이나 한적한 길을 걸으며 마음을 비워보세요",
                detailedDescription = "30분 동안 천천히 걸으며 주변 풍경을 감상하고 깊게 숨을 쉬어보세요. 스마트폰은 최대한 보지 말고 걷는 것 자체에 집중해보세요.",
                imageUrl = "https://images.unsplash.com/photo-1597644568217-780bd0b0efb2",
                tips = listOf(
                    "편안한 운동화를 신으세요",
                    "물병을 가져가세요",
                    "날씨에 맞는 옷을 입으세요",
                    "안전한 경로를 선택하세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.HEALTH,
                difficulty = MissionDifficulty.HARD,
                title = "1시간 동안 운동하기",
                description = "헬스장, 요가, 달리기 중 원하는 운동을 선택해 1시간 해보세요",
                detailedDescription = "헬스장에서 웨이트 트레이닝, 집에서 홈트레이닝, 공원에서 달리기, 요가원에서 요가 등 자신이 좋아하는 운동을 1시간 동안 해보세요.",
                imageUrl = "https://images.unsplash.com/photo-1597644568217-780bd0b0efb2",
                tips = listOf(
                    "운동 전 충분히 스트레칭하세요",
                    "수분 보충을 자주 하세요",
                    "몸의 신호를 잘 들어보세요",
                    "운동 후 쿨다운을 잊지 마세요"
                )
            ),

            // 창의적 미션들
            MissionTemplate(
                category = MissionCategory.CREATIVE,
                difficulty = MissionDifficulty.EASY,
                title = "사진으로 하루 기록하기",
                description = "오늘 하루를 사진 10장으로 기록해보세요",
                detailedDescription = "아침부터 저녁까지, 일상 속 특별한 순간들을 사진으로 담아보세요. 음식, 풍경, 사람, 감정 등 무엇이든 좋습니다. 10장의 사진으로 오늘 하루를 이야기해보세요.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "특별하지 않은 순간도 특별하게 담아보세요",
                    "다양한 각도에서 찍어보세요",
                    "빛을 잘 활용해보세요",
                    "스토리가 있는 사진을 찍어보세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.CREATIVE,
                difficulty = MissionDifficulty.MEDIUM,
                title = "일상 사물로 예술 작품 만들기",
                description = "주변에 있는 물건들로 창의적인 작품을 만들어보세요",
                detailedDescription = "집에 있는 일상 용품들(펜, 책, 컵, 화분 등)을 이용해 나만의 작품을 만들어보세요. 조각, 설치 미술, 콜라주 등 어떤 형태든 상관없습니다.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "완벽하지 않아도 괜찮아요",
                    "과정을 즐기세요",
                    "다양한 재료를 섞어보세요",
                    "작품에 의미를 부여해보세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.CREATIVE,
                difficulty = MissionDifficulty.HARD,
                title = "짧은 소설 한 편 쓰기",
                description = "1000자 내외의 짧은 소설을 써보세요",
                detailedDescription = "오늘 겪은 일, 상상 속의 이야기, 또는 주변 사람들에서 영감을 받아 1000자 정도의 짧은 소설을 써보세요. 완벽하지 않아도 괜찮습니다.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "처음과 끝을 정하고 시작하세요",
                    "한 번에 완성하려 하지 마세요",
                    "대화를 많이 넣어보세요",
                    "감정을 구체적으로 묘사해보세요"
                )
            ),

            // 학습 미션들
            MissionTemplate(
                category = MissionCategory.LEARNING,
                difficulty = MissionDifficulty.EASY,
                title = "새로운 단어 5개 배우기",
                description = "모르던 단어들을 찾아서 뜻을 익히고 문장을 만들어보세요",
                detailedDescription = "책, 뉴스, 인터넷에서 모르는 단어 5개를 찾아 사전에서 뜻을 찾아보고, 각각을 사용해 문장을 만들어보세요. 오늘 대화에서 사용해보면 더 좋습니다.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "실생활에서 쓸 수 있는 단어를 선택하세요",
                    "여러 번 반복해서 읽어보세요",
                    "오늘 대화에서 사용해보세요",
                    "단어장에 기록해두세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.LEARNING,
                difficulty = MissionDifficulty.MEDIUM,
                title = "새로운 요리 레시피 배우기",
                description = "30분 동안 새로운 요리 레시피를 도전해보세요",
                detailedDescription = "유튜브나 요리책에서 만들어보지 않은 요리를 하나 골라 따라해보세요. 실패해도 괜찮으니 과정을 즐겨보세요.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "재료를 미리 준비하세요",
                    "처음엔 간단한 요리부터 시작하세요",
                    "실패를 두려워하지 마세요",
                    "과정을 사진으로 기록해보세요"
                )
            ),
            MissionTemplate(
                category = MissionCategory.LEARNING,
                difficulty = MissionDifficulty.HARD,
                title = "1시간 동안 새로운 기술 배우기",
                description = "온라인 강의나 튜토리얼을 통해 새로운 스킬을 익혀보세요",
                detailedDescription = "유튜브, 온라인 강의 플랫폼에서 관심 있던 기술이나 스킬(포토샵, 프로그래밍, 외국어, 악기 등)을 1시간 동안 배워보세요.",
                imageUrl = "https://images.unsplash.com/photo-1549185545-f5b8a1fc481a",
                tips = listOf(
                    "기초부터 차근차근 시작하세요",
                    "실습 위주로 진행하세요",
                    "필기를 하며 배워보세요",
                    "궁금한 점은 바로 찾아보세요"
                )
            )
        )
    }
}

/**
 * 미션 템플릿 데이터 클래스
 */
data class MissionTemplate(
    val category: MissionCategory,
    val difficulty: MissionDifficulty,
    val title: String,
    val description: String,
    val detailedDescription: String,
    val imageUrl: String,
    val tips: List<String>
)