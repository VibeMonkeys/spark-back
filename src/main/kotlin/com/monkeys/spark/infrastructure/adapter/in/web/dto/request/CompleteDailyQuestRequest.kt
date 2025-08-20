package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 일일 퀘스트 완료 요청 DTO
 * "삶을 게임처럼 즐겨라!" - 퀘스트 완료 처리 요청
 */
data class CompleteDailyQuestRequest(
    val userId: Long,
    @JsonProperty("questId", required = false)
    val questId: String? = null, // 퀘스트 ID로 완료 처리 (더 유연한 방식)
    @JsonProperty("questType", required = false) 
    val questType: String? = null // 호환성을 위해 questType도 지원 (deprecated)
) {
    init {
        require(questId != null || questType != null) {
            "questId 또는 questType 중 하나는 반드시 제공되어야 합니다."
        }
    }
}

/**
 * 일일 퀘스트 템플릿 생성 요청 DTO (관리자용)
 */
data class CreateDailyQuestTemplateRequest(
    val questType: String,
    val title: String,
    val description: String,
    val icon: String,
    val order: Int,
    val rewardPoints: Int = 5,
    val isActive: Boolean = true
)

/**
 * 일일 퀘스트 템플릿 수정 요청 DTO (관리자용)
 */
data class UpdateDailyQuestTemplateRequest(
    val questType: String,
    val title: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val order: Int? = null,
    val rewardPoints: Int? = null,
    val isActive: Boolean? = null
)