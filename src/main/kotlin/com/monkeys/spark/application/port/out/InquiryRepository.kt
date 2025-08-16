package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.Inquiry
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus

interface InquiryRepository {
    
    fun save(inquiry: Inquiry): Inquiry
    
    fun findById(id: InquiryId): Inquiry?

    fun findByUserId(userId: UserId, page: Int = 0, size: Int = 20): List<Inquiry>
    
    fun findByStatus(status: InquiryStatus, page: Int = 0, size: Int = 20): List<Inquiry>
    
    fun findAll(page: Int = 0, size: Int = 20): List<Inquiry>
    
    fun countByStatus(status: InquiryStatus): Long
    
    fun delete(inquiry: Inquiry)
}