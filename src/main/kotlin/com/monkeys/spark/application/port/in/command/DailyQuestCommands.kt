package com.monkeys.spark.application.port.`in`.command

import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.dailyquest.DailyQuestType
import java.time.LocalDate

/**
 * 일일 퀘스트 관련 Command 클래스들
 * "삶을 게임처럼 즐겨라!" - 일상을 게임화하는 명령들
 */

/**
 * 일일 퀘스트 완료 Command
 */
data class CompleteDailyQuestCommand(
    val userId: UserId,
    val questType: DailyQuestType,
    val date: LocalDate = LocalDate.now()
)

/**
 * 일일 퀘스트 완료 취소 Command
 */
data class UncompleteDailyQuestCommand(
    val userId: UserId,
    val questType: DailyQuestType,
    val date: LocalDate = LocalDate.now()
)

/**
 * 사용자 일일 퀘스트 초기화 Command (매일 자정 실행)
 */
data class InitializeDailyQuestsCommand(
    val userId: UserId,
    val date: LocalDate = LocalDate.now()
)

/**
 * 전체 사용자 일일 퀘스트 초기화 Command (스케줄러용)
 */
data class InitializeAllUsersDailyQuestsCommand(
    val date: LocalDate = LocalDate.now()
)

/**
 * 일일 퀘스트 템플릿 생성 Command (관리자용)
 */
data class CreateDailyQuestTemplateCommand(
    val questType: DailyQuestType,
    val title: String,
    val description: String,
    val icon: String,
    val order: Int,
    val rewardPoints: Int = 5,
    val isActive: Boolean = true
)

/**
 * 일일 퀘스트 템플릿 수정 Command (관리자용)
 */
data class UpdateDailyQuestTemplateCommand(
    val questType: DailyQuestType,
    val title: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val order: Int? = null,
    val rewardPoints: Int? = null,
    val isActive: Boolean? = null
)

/**
 * 특수 보상 지급 Command (진행률 달성 시)
 */
data class GrantSpecialRewardCommand(
    val userId: UserId,
    val completionPercentage: Int,
    val date: LocalDate = LocalDate.now()
)