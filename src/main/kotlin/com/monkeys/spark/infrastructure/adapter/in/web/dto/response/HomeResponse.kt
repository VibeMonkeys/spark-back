package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 홈페이지 데이터 응답 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class HomePageResponse(
    @JsonProperty("user_summary")
    val userSummary: UserSummaryResponse,
    @JsonProperty("todays_missions")
    val todaysMissions: List<MissionResponse>,
    @JsonProperty("recent_stories")
    val recentStories: List<StoryResponse>
)