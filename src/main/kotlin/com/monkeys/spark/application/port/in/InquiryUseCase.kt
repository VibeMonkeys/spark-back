package com.monkeys.spark.application.port.`in`

import com.monkeys.spark.application.port.`in`.command.CreateInquiryCommand
import com.monkeys.spark.application.port.`in`.command.RespondToInquiryCommand
import com.monkeys.spark.domain.model.Inquiry
import com.monkeys.spark.domain.vo.common.UserId
import com.monkeys.spark.domain.vo.inquiry.InquiryId
import com.monkeys.spark.domain.vo.inquiry.InquiryStatus

interface InquiryUseCase {
    
    /**
     * 문의 생성
     */
    fun createInquiry(command: CreateInquiryCommand): Inquiry
    
    /**
     * 문의 조회
     */
    fun getInquiry(inquiryId: InquiryId): Inquiry?
    
    /**
     * 사용자별 문의 목록 조회
     */
    fun getUserInquiries(userId: UserId, page: Int = 0, size: Int = 20): List<Inquiry>
    
    /**
     * 문의 응답 (관리자용)
     */
    fun respondToInquiry(command: RespondToInquiryCommand): Inquiry
    
    /**
     * 상태별 문의 목록 조회 (관리자용)
     */
    fun getInquiriesByStatus(status: InquiryStatus, page: Int = 0, size: Int = 20): List<Inquiry>
    
    /**
     * 모든 문의 목록 조회 (관리자용)
     */
    fun getAllInquiries(page: Int = 0, size: Int = 20): List<Inquiry>
    
    /**
     * 상태별 문의 개수 조회
     */
    fun getInquiryCountByStatus(status: InquiryStatus): Long
}