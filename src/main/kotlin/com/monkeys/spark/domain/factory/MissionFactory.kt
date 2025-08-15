package com.monkeys.spark.domain.factory

import com.monkeys.spark.application.port.out.MissionRepository
import com.monkeys.spark.domain.model.Mission
import com.monkeys.spark.domain.model.MissionConditions
import com.monkeys.spark.domain.model.User
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.mission.*
import org.springframework.stereotype.Component

@Component
class MissionFactory(
    private val missionRepository: MissionRepository
) {

    /**
     * 사용자별 일일 미션 3개 생성
     */
    fun createDailyMissions(user: User): List<Mission> {
        // DB에서 템플릿 미션들 조회
        val templateMissions = missionRepository.findTemplateMissions()

        // 사용자 선호도 기반으로 미션 필터링
        val availableCategories = user.preferences.filter { it.value }.keys.toList()
        val filteredMissions = if (availableCategories.isNotEmpty()) {
            templateMissions.filter { it.category in availableCategories }
        } else {
            templateMissions
        }

        // 난이도별로 1개씩 선택
        val easyMission = filteredMissions.filter { it.difficulty == MissionDifficulty.EASY }.randomOrNull()
        val mediumMission = filteredMissions.filter { it.difficulty == MissionDifficulty.MEDIUM }.randomOrNull()
        val hardMission = filteredMissions.filter { it.difficulty == MissionDifficulty.HARD }.randomOrNull()

        val selectedMissions = listOfNotNull(easyMission, mediumMission, hardMission)

        // 3개가 안되면 남은 템플릿에서 랜덤 선택
        val missions = if (selectedMissions.size < 3) {
            val remaining = filteredMissions - selectedMissions.toSet()
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
    private fun createPersonalizedMission(user: User, template: Mission): Mission {
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

}