package com.monkeys.spark.domain.vo.common

import java.util.*

/**
 * 해시태그 통계 ID Value Object
 */
@JvmInline
value class HashtagStatsId(val value: String) {
    companion object {
        fun generate(): HashtagStatsId = HashtagStatsId(UUID.randomUUID().toString())
        
        fun from(value: String): HashtagStatsId = HashtagStatsId(value)
    }
}