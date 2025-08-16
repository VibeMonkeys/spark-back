package com.monkeys.spark.domain.vo.inquiry

import java.util.UUID

@JvmInline
value class InquiryId(val value: String) {
    companion object {
        fun generate(): InquiryId = InquiryId("inquiry_${UUID.randomUUID()}")
    }
    
    init {
        require(value.isNotBlank()) { "InquiryId cannot be blank" }
    }
}