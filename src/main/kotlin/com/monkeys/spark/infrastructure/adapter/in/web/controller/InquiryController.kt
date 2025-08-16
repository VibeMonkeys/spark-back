package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.port.`in`.InquiryUseCase
import com.monkeys.spark.application.port.`in`.command.CreateInquiryCommand
import com.monkeys.spark.application.port.`in`.command.RespondToInquiryCommand
import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.CreateInquiryRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.RespondToInquiryRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.InquiryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inquiries")
class InquiryController(
    private val inquiryUseCase: InquiryUseCase,
    private val responseMapper: ResponseMapper
) {

    @PostMapping
    fun createInquiry(
        @RequestBody request: CreateInquiryRequest
    ): ResponseEntity<ApiResponse<InquiryResponse>> {
        try {
            val command = CreateInquiryCommand(
                userId = request.userId,
                email = request.email,
                subject = request.subject,
                message = request.message
            )
            
            val inquiry = inquiryUseCase.createInquiry(command)
            val response = responseMapper.toInquiryResponse(inquiry)
            
            return ResponseEntity.ok(ApiResponse.success(response, "문의가 성공적으로 접수되었습니다."))
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<InquiryResponse>(
                    "문의 접수에 실패했습니다: ${e.message}", 
                    "INQUIRY_CREATE_FAILED"
                )
            )
        }
    }

    @GetMapping("/{inquiryId}")
    fun getInquiry(
        @PathVariable inquiryId: String
    ): ResponseEntity<ApiResponse<InquiryResponse>> {
        try {
            val inquiry = inquiryUseCase.getInquiry(InquiryId(inquiryId))
                ?: return ResponseEntity.ok(
                    ApiResponse.error<InquiryResponse>(
                        "문의를 찾을 수 없습니다.", 
                        "INQUIRY_NOT_FOUND"
                    )
                )
            
            val response = responseMapper.toInquiryResponse(inquiry)
            return ResponseEntity.ok(ApiResponse.success(response))
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<InquiryResponse>(
                    "문의 조회에 실패했습니다: ${e.message}", 
                    "INQUIRY_GET_FAILED"
                )
            )
        }
    }

    @GetMapping("/user/{userId}")
    fun getUserInquiries(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<InquiryResponse>>> {
        try {
            val inquiries = inquiryUseCase.getUserInquiries(UserId(userId), page, size)
            val responses = inquiries.map { responseMapper.toInquiryResponse(it) }
            
            return ResponseEntity.ok(ApiResponse.success(responses))
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<List<InquiryResponse>>(
                    "사용자 문의 목록 조회에 실패했습니다: ${e.message}", 
                    "USER_INQUIRIES_GET_FAILED"
                )
            )
        }
    }

    @PostMapping("/{inquiryId}/respond")
    fun respondToInquiry(
        @PathVariable inquiryId: String,
        @RequestBody request: RespondToInquiryRequest
    ): ResponseEntity<ApiResponse<InquiryResponse>> {
        try {
            val command = RespondToInquiryCommand(
                inquiryId = inquiryId,
                response = request.response,
                respondedBy = request.respondedBy
            )
            
            val inquiry = inquiryUseCase.respondToInquiry(command)
            val response = responseMapper.toInquiryResponse(inquiry)
            
            return ResponseEntity.ok(ApiResponse.success(response, "문의에 성공적으로 응답했습니다."))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.ok(
                ApiResponse.error<InquiryResponse>(
                    e.message ?: "문의 응답에 실패했습니다.", 
                    "INQUIRY_RESPOND_FAILED"
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<InquiryResponse>(
                    "문의 응답에 실패했습니다: ${e.message}", 
                    "INQUIRY_RESPOND_FAILED"
                )
            )
        }
    }

    @GetMapping("/status/{status}")
    fun getInquiriesByStatus(
        @PathVariable status: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<InquiryResponse>>> {
        try {
            val inquiryStatus = InquiryStatus.valueOf(status.uppercase())
            val inquiries = inquiryUseCase.getInquiriesByStatus(inquiryStatus, page, size)
            val responses = inquiries.map { responseMapper.toInquiryResponse(it) }
            
            return ResponseEntity.ok(ApiResponse.success(responses))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.ok(
                ApiResponse.error<List<InquiryResponse>>(
                    "잘못된 상태값입니다: $status", 
                    "INVALID_STATUS"
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<List<InquiryResponse>>(
                    "상태별 문의 목록 조회에 실패했습니다: ${e.message}", 
                    "STATUS_INQUIRIES_GET_FAILED"
                )
            )
        }
    }

    @GetMapping
    fun getAllInquiries(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<InquiryResponse>>> {
        try {
            val inquiries = inquiryUseCase.getAllInquiries(page, size)
            val responses = inquiries.map { responseMapper.toInquiryResponse(it) }
            
            return ResponseEntity.ok(ApiResponse.success(responses))
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<List<InquiryResponse>>(
                    "전체 문의 목록 조회에 실패했습니다: ${e.message}", 
                    "ALL_INQUIRIES_GET_FAILED"
                )
            )
        }
    }

    @GetMapping("/count/{status}")
    fun getInquiryCountByStatus(
        @PathVariable status: String
    ): ResponseEntity<ApiResponse<Long>> {
        try {
            val inquiryStatus = InquiryStatus.valueOf(status.uppercase())
            val count = inquiryUseCase.getInquiryCountByStatus(inquiryStatus)
            
            return ResponseEntity.ok(ApiResponse.success(count))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.ok(
                ApiResponse.error<Long>(
                    "잘못된 상태값입니다: $status", 
                    "INVALID_STATUS"
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.ok(
                ApiResponse.error<Long>(
                    "문의 개수 조회에 실패했습니다: ${e.message}", 
                    "INQUIRY_COUNT_FAILED"
                )
            )
        }
    }
}