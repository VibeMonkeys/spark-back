package com.monkeys.spark.domain.model

import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.*
import com.monkeys.spark.domain.vo.mission.*
import com.monkeys.spark.domain.vo.story.*

/**
 * Story Feed Item - for representing stories in the feed
 */
data class StoryFeedItem(
    val storyId: StoryId,
    val user: StoryUser,
    val mission: StoryMission,
    val content: StoryContent,
    val interactions: StoryInteractions,
    val timeAgo: String,
    val location: Location
)

data class StoryUser(
    val userId: UserId,
    val name: UserName,
    val avatarUrl: AvatarUrl,
    val level: Level,
    val levelTitle: UserLevelTitle
)

data class StoryMission(
    val missionId: MissionId,
    val title: MissionTitle,
    val category: MissionCategory
)

data class StoryContent(
    val storyText: StoryText,
    val images: List<ImageUrl>,
    val tags: List<HashTag>
)

data class StoryInteractions(
    val likes: LikeCount,
    val comments: CommentCount,
    val isLikedByCurrentUser: Boolean = false
)