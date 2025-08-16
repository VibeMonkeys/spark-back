package com.monkeys.spark.infrastructure.adapter.`in`.web.dto.request

data class CreateInquiryRequest(
    val userId: Long? = null,
    val email: String,
    val subject: String,
    val message: String
)

data class RespondToInquiryRequest(
    val response: String,
    val respondedBy: String
)