package com.monkeys.spark.infrastructure.adapter.`in`.web.controller

import com.monkeys.spark.application.mapper.ResponseMapper
import com.monkeys.spark.application.port.`in`.InquiryUseCase
import com.monkeys.spark.application.port.`in`.command.CreateInquiryCommand
import com.monkeys.spark.application.port.`in`.command.RespondToInquiryCommand
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.CreateInquiryRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request.RespondToInquiryRequest
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.response.InquiryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inquiries")
class InquiryController(
    private val inquiryUseCase: InquiryUseCase,
    private val responseMapper: ResponseMapper
) {

    /**
     * 문의 생성
     * POST /api/inquiries
     */
    @PostMapping
    fun createInquiry(
        @RequestBody request: CreateInquiryRequest
    ): ResponseEntity<ApiResponse<InquiryResponse>> {
        val command = CreateInquiryCommand(
            userId = request.userId,
            email = request.email,
            subject = request.subject,
            message = request.message
        )

        val inquiry = inquiryUseCase.createInquiry(command)
        val response = responseMapper.toInquiryResponse(inquiry)

        return ResponseEntity.ok(ApiResponse.success(response, "문의가 성공적으로 접수되었습니다."))
    }

    @GetMapping("/{inquiryId}")
    fun getInquiry(
        @PathVariable inquiryId: Long
    ): ResponseEntity<ApiResponse<InquiryResponse>> {
        val inquiry = inquiryUseCase.getInquiry(InquiryId(inquiryId))
            ?: return ResponseEntity.ok(
                ApiResponse.error<InquiryResponse>(
                    "문의를 찾을 수 없습니다.",
                    "INQUIRY_NOT_FOUND"
                )
            )

        val response = responseMapper.toInquiryResponse(inquiry)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/user/{userId}")
    fun getUserInquiries(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<InquiryResponse>>> {
        val inquiries = inquiryUseCase.getUserInquiries(UserId(userId), page, size)
        val responses = inquiries.map { responseMapper.toInquiryResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(responses))
    }

    @PostMapping("/{inquiryId}/respond")
    fun respondToInquiry(
        @PathVariable inquiryId: Long,
        @RequestBody request: RespondToInquiryRequest
    ): ResponseEntity<ApiResponse<InquiryResponse>> {
        val command = RespondToInquiryCommand(
            inquiryId = inquiryId,
            response = request.response,
            respondedBy = request.respondedBy
        )

        val inquiry = inquiryUseCase.respondToInquiry(command)
        val response = responseMapper.toInquiryResponse(inquiry)

        return ResponseEntity.ok(ApiResponse.success(response, "문의에 성공적으로 응답했습니다."))
    }

    @GetMapping("/status/{status}")
    fun getInquiriesByStatus(
        @PathVariable status: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<InquiryResponse>>> {
        val inquiryStatus = InquiryStatus.valueOf(status.uppercase())
        val inquiries = inquiryUseCase.getInquiriesByStatus(inquiryStatus, page, size)
        val responses = inquiries.map { responseMapper.toInquiryResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(responses))
    }

    @GetMapping
    fun getAllInquiries(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<InquiryResponse>>> {
        val inquiries = inquiryUseCase.getAllInquiries(page, size)
        val responses = inquiries.map { responseMapper.toInquiryResponse(it) }

        return ResponseEntity.ok(ApiResponse.success(responses))
    }

    @GetMapping("/count/{status}")
    fun getInquiryCountByStatus(
        @PathVariable status: String
    ): ResponseEntity<ApiResponse<Long>> {
        val inquiryStatus = InquiryStatus.valueOf(status.uppercase())
        val count = inquiryUseCase.getInquiryCountByStatus(inquiryStatus)

        return ResponseEntity.ok(ApiResponse.success(count))
    }

}