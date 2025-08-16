package com.monkeys.spark.domain.vo.inquiry

import java.util.UUID

@JvmInline
value class InquiryId(val value: Long) {
    companion object {
        fun generate(): InquiryId = InquiryId(0L) // Auto-increment will assign the actual ID
    }
    
    init {
        require(value >= 0) { "InquiryId must be non-negative" }
    }
}