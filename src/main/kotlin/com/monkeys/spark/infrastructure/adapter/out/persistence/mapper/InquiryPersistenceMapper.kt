package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.Inquiry
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.InquiryEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.InquiryStatusEntity
import org.springframework.stereotype.Component

@Component
class InquiryPersistenceMapper {
    
    fun toEntity(domain: Inquiry): InquiryEntity {
        return InquiryEntity(
            id = domain.id.value,
            userId = domain.userId?.value,
            email = domain.email,
            subject = domain.subject,
            message = domain.message,
            status = toEntityStatus(domain.status),
            response = domain.response,
            respondedAt = domain.respondedAt,
            respondedBy = domain.respondedBy,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun toDomain(entity: InquiryEntity): Inquiry {
        return Inquiry(
            id = InquiryId(entity.id),
            userId = entity.userId?.let { UserId(it) },
            email = entity.email,
            subject = entity.subject,
            message = entity.message,
            status = toDomainStatus(entity.status),
            response = entity.response,
            respondedAt = entity.respondedAt,
            respondedBy = entity.respondedBy,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    private fun toEntityStatus(status: InquiryStatus): InquiryStatusEntity {
        return when (status) {
            InquiryStatus.PENDING -> InquiryStatusEntity.PENDING
            InquiryStatus.IN_PROGRESS -> InquiryStatusEntity.IN_PROGRESS
            InquiryStatus.RESPONDED -> InquiryStatusEntity.RESPONDED
            InquiryStatus.CLOSED -> InquiryStatusEntity.CLOSED
        }
    }
    
    private fun toDomainStatus(status: InquiryStatusEntity): InquiryStatus {
        return when (status) {
            InquiryStatusEntity.PENDING -> InquiryStatus.PENDING
            InquiryStatusEntity.IN_PROGRESS -> InquiryStatus.IN_PROGRESS
            InquiryStatusEntity.RESPONDED -> InquiryStatus.RESPONDED
            InquiryStatusEntity.CLOSED -> InquiryStatus.CLOSED
        }
    }
}