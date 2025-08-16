package com.monkeys.spark.domain.factory

import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.MissionConditions
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*

/**
 * 순수한 도메인 Factory - 외부 의존성 없음
 */
class MissionFactory {

    /**
     * 사용자별 일일 미션 5개 생성
     */
    fun createDailyMissions(user: User, templateMissions: List<Mission>): List<Mission> {

        // 사용자 선호도 기반으로 미션 필터링
        val availableCategories = user.preferences.filter { it.value }.keys.toList()
        val filteredMissions = if (availableCategories.isNotEmpty()) {
            templateMissions.filter { it.category in availableCategories }
        } else {
            templateMissions
        }

        // 난이도별로 균형있게 선택 (Easy 2개, Medium 2개, Hard 1개)
        val easyMissions = filteredMissions.filter { it.difficulty == MissionDifficulty.EASY }.shuffled().take(2)
        val mediumMissions = filteredMissions.filter { it.difficulty == MissionDifficulty.MEDIUM }.shuffled().take(2)
        val hardMissions = filteredMissions.filter { it.difficulty == MissionDifficulty.HARD }.shuffled().take(1)

        var selectedMissions = easyMissions + mediumMissions + hardMissions

        // 5개가 안되면 남은 템플릿에서 랜덤 선택
        if (selectedMissions.size < 5) {
            val remaining = filteredMissions - selectedMissions.toSet()
            selectedMissions = selectedMissions + remaining.shuffled().take(5 - selectedMissions.size)
        }

        // 사용자별 미션으로 변환
        return selectedMissions.take(5).map { template ->
            createPersonalizedMission(user, template)
        }
    }

    /**
     * 개인화된 미션 생성
     */
    private fun createPersonalizedMission(user: User, template: Mission): Mission {
        return Mission.create(
            userId = user.id,
            title = template.title.value,
            description = template.description.value,
            detailedDescription = template.detailedDescription.value,
            category = template.category,
            difficulty = template.difficulty,
            imageUrl = template.imageUrl.value,
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

}