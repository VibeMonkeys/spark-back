package com.monkeys.spark.domain.vo.hashtag

/**
 * 해시태그 검색 결과 정렬 기준
 */
enum class HashtagSortCriteria {
    RELEVANCE,      // 관련성 (기본값)
    POPULARITY,     // 인기도
    RECENT,         // 최근 사용
    ALPHABETICAL,   // 알파벳순
    USAGE_COUNT     // 사용 횟수
}