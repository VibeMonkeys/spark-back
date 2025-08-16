package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import java.time.LocalDateTime

/**
 * 문의 도메인 모델
 */
data class Inquiry(
    val id: InquiryId,
    val userId: UserId?,  // 비회원 문의도 가능하므로 nullable
    val email: String,
    val subject: String,
    val message: String,
    val status: InquiryStatus = InquiryStatus.PENDING,
    val response: String? = null,
    val respondedAt: LocalDateTime? = null,
    val respondedBy: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            userId: UserId?,
            email: String,
            subject: String,
            message: String
        ): Inquiry {
            require(email.isNotBlank()) { "Email cannot be blank" }
            require(subject.isNotBlank()) { "Subject cannot be blank" }
            require(message.isNotBlank()) { "Message cannot be blank" }
            require(subject.length <= 200) { "Subject must be 200 characters or less" }
            require(message.length <= 2000) { "Message must be 2000 characters or less" }
            
            return Inquiry(
                id = InquiryId.generate(),
                userId = userId,
                email = email.trim(),
                subject = subject.trim(),
                message = message.trim()
            )
        }
    }
    
    fun respond(response: String, respondedBy: String): Inquiry {
        require(response.isNotBlank()) { "Response cannot be blank" }
        require(respondedBy.isNotBlank()) { "Responder cannot be blank" }
        
        return this.copy(
            status = InquiryStatus.RESPONDED,
            response = response.trim(),
            respondedAt = LocalDateTime.now(),
            respondedBy = respondedBy,
            updatedAt = LocalDateTime.now()
        )
    }
    
    fun close(): Inquiry {
        return this.copy(
            status = InquiryStatus.CLOSED,
            updatedAt = LocalDateTime.now()
        )
    }
}