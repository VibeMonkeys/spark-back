package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.Inquiry
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.InquiryEntity
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
            status = domain.status,
            response = domain.response,
            respondedAt = domain.respondedAt,
            respondedBy = domain.respondedBy
        )
    }
    
    fun toDomain(entity: InquiryEntity): Inquiry {
        return Inquiry(
            id = InquiryId(entity.id),
            userId = entity.userId?.let { UserId(it) },
            email = entity.email,
            subject = entity.subject,
            message = entity.message,
            status = entity.status,
            response = entity.response,
            respondedAt = entity.respondedAt,
            respondedBy = entity.respondedBy,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}