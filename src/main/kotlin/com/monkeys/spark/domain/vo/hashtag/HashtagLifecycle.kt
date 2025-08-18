package com.monkeys.spark.domain.vo.hashtag

/**
 * 해시태그 생명주기
 */
enum class HashtagLifecycle(val displayName: String) {
    EMERGING("새롭게 떠오르는"),
    TRENDING("급상승 중"),
    MATURE("안정적 인기"),
    STABLE("일정한 사용량"),
    DECLINING("사용량 감소")
}