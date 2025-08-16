package com.monkeys.spark.infrastructure.adapter.out.persistence.entity

import com.monkeys.spark.domain.vo.inquiry.InquiryStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inquiries")
data class InquiryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0L,

    @Column(name = "user_id", nullable = true)
    var userId: Long? = null,

    @Column(name = "email", length = 255, nullable = false)
    var email: String = "",

    @Column(name = "subject", length = 200, nullable = false)
    var subject: String = "",

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    var message: String = "",

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: InquiryStatus = InquiryStatus.PENDING,

    @Column(name = "response", columnDefinition = "TEXT", nullable = true)
    var response: String? = null,

    @Column(name = "responded_at", nullable = true)
    var respondedAt: LocalDateTime? = null,

    @Column(name = "responded_by", length = 100, nullable = true)
    var respondedBy: String? = null
) : BaseEntity()