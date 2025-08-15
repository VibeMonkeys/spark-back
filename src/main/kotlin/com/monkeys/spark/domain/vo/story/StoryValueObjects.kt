package com.monkeys.spark.domain.vo.story

// 스토리 관련 Value Objects
@JvmInline
value class StoryText(val value: String) {
    init {
        require(value.length in 10..500) { "Story text must be between 10 and 500 characters" }
    }
}

@JvmInline
value class LikeCount(val value: Int) {
    init {
        require(value >= 0) { "Like count cannot be negative" }
    }
    
    fun increment(): LikeCount = LikeCount(value + 1)
    fun decrement(): LikeCount = LikeCount(maxOf(0, value - 1))
}

@JvmInline
value class CommentCount(val value: Int) {
    init {
        require(value >= 0) { "Comment count cannot be negative" }
    }
    
    fun increment(): CommentCount = CommentCount(value + 1)
}

@JvmInline
value class HashTag(val value: String) {
    init {
        require(value.isNotBlank()) { "HashTag cannot be blank" }
        require(value.startsWith("#")) { "HashTag must start with #" }
        require(value.length <= 50) { "HashTag cannot exceed 50 characters" }
    }
}