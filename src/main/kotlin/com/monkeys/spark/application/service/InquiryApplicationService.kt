package com.monkeys.spark.application.service

import com.monkeys.spark.application.port.`in`.InquiryUseCase
import com.monkeys.spark.application.port.`in`.command.CreateInquiryCommand
import com.monkeys.spark.application.port.`in`.command.RespondToInquiryCommand
import com.monkeys.spark.application.port.out.InquiryRepository
import com.monkeys.spark.domain.model.Inquiry
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InquiryApplicationService(
    private val inquiryRepository: InquiryRepository
) : InquiryUseCase {
    
    override fun createInquiry(command: CreateInquiryCommand): Inquiry {
        val userId = command.userId?.let { UserId(it) }
        
        val inquiry = Inquiry.create(
            userId = userId,
            email = command.email,
            subject = command.subject,
            message = command.message
        )
        
        return inquiryRepository.save(inquiry)
    }
    
    @Transactional(readOnly = true)
    override fun getInquiry(inquiryId: InquiryId): Inquiry? {
        return inquiryRepository.findById(inquiryId)
    }
    
    @Transactional(readOnly = true)
    override fun getUserInquiries(userId: UserId, page: Int, size: Int): List<Inquiry> {
        return inquiryRepository.findByUserId(userId, page, size)
    }
    
    override fun respondToInquiry(command: RespondToInquiryCommand): Inquiry {
        val inquiry = inquiryRepository.findById(InquiryId(command.inquiryId))
            ?: throw IllegalArgumentException("Inquiry not found: ${command.inquiryId}")
        
        val respondedInquiry = inquiry.respond(
            response = command.response,
            respondedBy = command.respondedBy
        )
        
        return inquiryRepository.save(respondedInquiry)
    }
    
    @Transactional(readOnly = true)
    override fun getInquiriesByStatus(status: InquiryStatus, page: Int, size: Int): List<Inquiry> {
        return inquiryRepository.findByStatus(status, page, size)
    }
    
    @Transactional(readOnly = true)
    override fun getAllInquiries(page: Int, size: Int): List<Inquiry> {
        return inquiryRepository.findAll(page, size)
    }
    
    @Transactional(readOnly = true)
    override fun getInquiryCountByStatus(status: InquiryStatus): Long {
        return inquiryRepository.countByStatus(status)
    }
}