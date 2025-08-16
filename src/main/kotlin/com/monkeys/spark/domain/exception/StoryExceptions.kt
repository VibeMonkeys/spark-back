package com.monkeys.spark.domain.exception

class StoryNotFoundException(storyId: String) : EntityNotFoundException("Story", storyId, "STORY_NOT_FOUND")

class StoryAccessDeniedException(message: String = "Access denied to story") : DomainException(
    message,
    "STORY_ACCESS_DENIED"
)