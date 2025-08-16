package com.monkeys.spark.domain.vo.inquiry

enum class InquiryStatus(val displayName: String) {
    PENDING("접수대기"),
    IN_PROGRESS("처리중"),
    RESPONDED("답변완료"),
    CLOSED("종료")
}