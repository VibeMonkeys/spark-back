package com.monkeys.spark.infrastructure.adapter.`in`.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 커서 기반 페이지네이션 정보
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CursorPageInfo(
    @JsonProperty("has_next")
    val hasNext: Boolean,
    @JsonProperty("has_previous")
    val hasPrevious: Boolean,
    @JsonProperty("next_cursor")
    val nextCursor: String? = null,
    @JsonProperty("previous_cursor")
    val previousCursor: String? = null,
    @JsonProperty("page_size")
    val pageSize: Int
)

/**
 * 커서 기반 페이징된 응답 데이터
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CursorPagedResponse<T>(
    val items: List<T>,
    @JsonProperty("page_info")
    val pageInfo: CursorPageInfo
)

/**
 * 커서 페이지네이션 요청 파라미터
 */
data class CursorPageRequest(
    val cursor: String? = null,
    val size: Int = 20,
    val direction: CursorDirection = CursorDirection.NEXT
)

enum class CursorDirection {
    NEXT, PREVIOUS
}