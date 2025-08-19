package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

/**
 * 일일 퀘스트 완료 요청 DTO
 * "삶을 게임처럼 즐겨라!" - 퀘스트 완료 처리 요청
 */
data class CompleteDailyQuestRequest(
    val userId: Long,
    val questType: String // DailyQuestType enum 이름 (예: "MAKE_BED", "TAKE_SHOWER")
)

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