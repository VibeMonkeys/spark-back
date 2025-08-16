package com.monkeys.spark.application.port.`in`.command

/**
 * 문의 생성 커맨드
 */
data class CreateInquiryCommand(
    val userId: String?, // nullable for anonymous inquiries
    val email: String,
    val subject: String,
    val message: String
)

/**
 * 문의 응답 커맨드 (관리자용)
 */
data class RespondToInquiryCommand(
    val inquiryId: String,
    val response: String,
    val respondedBy: String
)