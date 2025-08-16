package com.monkeys.spark.domain.exception

class StoryNotFoundException(storyId: Long) : EntityNotFoundException("Story", storyId.toString(), "STORY_NOT_FOUND")

class StoryAccessDeniedException(message: String = "Access denied to story") : DomainException(
    message,
    "STORY_ACCESS_DENIED"
)