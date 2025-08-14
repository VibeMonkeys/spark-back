package com.monkeys.spark.infrastructure.adapter.`in`.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * 공통 API 응답 래퍼
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: ErrorDetail? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, data = data, message = message)
        }
        
        fun <T> success(message: String): ApiResponse<T> {
            return ApiResponse(success = true, message = message)
        }
        
        fun <T> error(message: String, errorCode: String? = null, details: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorDetail(
                    code = errorCode ?: "UNKNOWN_ERROR",
                    message = message,
                    details = details
                )
            )
        }
        
        fun <T> error(errorDetail: ErrorDetail): ApiResponse<T> {
            return ApiResponse(success = false, error = errorDetail)
        }
    }
}

/**
 * 에러 상세 정보
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDetail(
    val code: String,
    val message: String,
    val details: String? = null,
    val field: String? = null
)

/**
 * 페이징 정보
 */
data class PageInfo(
    @JsonProperty("current_page")
    val currentPage: Int,
    @JsonProperty("page_size")
    val pageSize: Int,
    @JsonProperty("total_elements")
    val totalElements: Long,
    @JsonProperty("total_pages")
    val totalPages: Int,
    @JsonProperty("has_next")
    val hasNext: Boolean,
    @JsonProperty("has_previous")
    val hasPrevious: Boolean
)

/**
 * 페이징된 응답 데이터
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PagedResponse<T>(
    val items: List<T>,
    @JsonProperty("page_info")
    val pageInfo: PageInfo
)