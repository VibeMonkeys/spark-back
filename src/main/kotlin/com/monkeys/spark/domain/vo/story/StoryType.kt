package com.monkeys.spark.domain.vo.story

import com.monkeys.spark.domain.exception.InvalidStoryTypeException

enum class StoryType(val displayName: String, val description: String) {
    FREE_STORY("자유 스토리", "일상을 자유롭게 공유하는 스토리"),
    MISSION_PROOF("미션 인증", "미션 완료를 인증하는 스토리");
    
    companion object {
        fun fromString(value: String): StoryType {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                throw InvalidStoryTypeException("Invalid story type: $value")
            }
        }
    }
}