package com.monkeys.spark.infrastructure.adapter.out.persistence

import com.monkeys.spark.application.port.out.InquiryRepository
import com.monkeys.spark.domain.model.Inquiry
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import com.monkeys.spark.infrastructure.adapter.out.persistence.mapper.InquiryPersistenceMapper
import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.JpaInquiryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class InquiryPersistenceAdapter(
    private val jpaInquiryRepository: JpaInquiryRepository,
    private val mapper: InquiryPersistenceMapper
) : InquiryRepository {

    override fun save(inquiry: Inquiry): Inquiry {
        val entity = mapper.toEntity(inquiry)
        val savedEntity = jpaInquiryRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findById(id: InquiryId): Inquiry? {
        return jpaInquiryRepository.findById(id.value)
            .map(mapper::toDomain)
            .orElse(null)
    }

    override fun findByUserId(userId: UserId, page: Int, size: Int): List<Inquiry> {
        val pageable = PageRequest.of(page, size)
        return jpaInquiryRepository.findByUserIdOrderByCreatedAtDesc(userId.value, pageable)
            .map(mapper::toDomain)
    }

    override fun findByStatus(status: InquiryStatus, page: Int, size: Int): List<Inquiry> {
        val entityStatus = toEntityStatus(status)
        val pageable = PageRequest.of(page, size)
        return jpaInquiryRepository.findByStatusOrderByCreatedAtDesc(entityStatus, pageable)
            .map(mapper::toDomain)
    }

    override fun findAll(page: Int, size: Int): List<Inquiry> {
        val pageable = PageRequest.of(page, size)
        return jpaInquiryRepository.findAllOrderByCreatedAtDesc(pageable)
            .map(mapper::toDomain)
    }

    override fun countByStatus(status: InquiryStatus): Long {
        val entityStatus = toEntityStatus(status)
        return jpaInquiryRepository.countByStatus(entityStatus)
    }

    override fun delete(inquiry: Inquiry) {
        jpaInquiryRepository.deleteById(inquiry.id.value)
    }

    private fun toEntityStatus(status: InquiryStatus): InquiryStatus {
        return when (status) {
            InquiryStatus.PENDING -> InquiryStatus.PENDING
            InquiryStatus.IN_PROGRESS -> InquiryStatus.IN_PROGRESS
            InquiryStatus.RESPONDED -> InquiryStatus.RESPONDED
            InquiryStatus.CLOSED -> InquiryStatus.CLOSED
        }
    }
}