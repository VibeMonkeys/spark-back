package com.monkeys.spark.domain.vo.common

// 이미지 URL Value Object
@JvmInline
value class ImageUrl(val value: String) {
    init {
        require(value.isNotBlank()) { "Image URL cannot be blank" }
        // URL 패턴 검증 생략 (실제로는 정규식 검증 추가 가능)
    }
}