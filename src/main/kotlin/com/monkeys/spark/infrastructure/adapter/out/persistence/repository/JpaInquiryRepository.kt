package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.InquiryEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaInquiryRepository : JpaRepository<InquiryEntity, Long> {

    fun findByUserIdOrderByCreatedAtDesc(userId: Long, pageable: Pageable): List<InquiryEntity>

    fun findByStatusOrderByCreatedAtDesc(status: InquiryStatus, pageable: Pageable): List<InquiryEntity>

    @Query("SELECT i FROM InquiryEntity i ORDER BY i.createdAt DESC")
    fun findAllOrderByCreatedAtDesc(pageable: Pageable): List<InquiryEntity>

    fun countByStatus(status: InquiryStatus): Long

}