package com.monkeys.spark.infrastructure.adapter.out.persistence.repository

import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.InquiryEntity
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.InquiryStatusEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JpaInquiryRepository : JpaRepository<InquiryEntity, String> {
    
    fun findByUserIdOrderByCreatedAtDesc(userId: String, pageable: Pageable): List<InquiryEntity>
    
    fun findByUserIdOrderByCreatedAtDesc(userId: String): List<InquiryEntity>
    
    fun findByStatusOrderByCreatedAtDesc(status: InquiryStatusEntity, pageable: Pageable): List<InquiryEntity>
    
    @Query("SELECT i FROM InquiryEntity i ORDER BY i.createdAt DESC")
    fun findAllOrderByCreatedAtDesc(pageable: Pageable): List<InquiryEntity>
    
    fun countByStatus(status: InquiryStatusEntity): Long
}