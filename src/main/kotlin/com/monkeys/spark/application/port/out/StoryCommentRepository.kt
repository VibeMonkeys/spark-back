package com.monkeys.spark.application.port.out

import com.monkeys.spark.domain.model.StoryComment
import com.monkeys.spark.domain.vo.common.StoryId

interface StoryCommentRepository {

    fun save(storyComment: StoryComment): StoryComment

    fun findByStoryId(storyId: StoryId): List<StoryComment>

}