package com.monkeys.spark.infrastructure.adapter.out.persistence.mapper

import com.monkeys.spark.domain.model.StoryComment
import com.monkeys.spark.domain.vo.common.*
import com.monkeys.spark.domain.vo.user.UserName
import com.monkeys.spark.domain.vo.user.AvatarUrl
import com.monkeys.spark.infrastructure.adapter.out.persistence.entity.StoryCommentEntity
import org.springframework.stereotype.Component

@Component
class StoryCommentPersistenceMapper {
    
    fun toEntity(domain: StoryComment): StoryCommentEntity {
        val entity = StoryCommentEntity()
        entity.id = domain.id
        entity.storyId = domain.storyId.value
        entity.userId = domain.userId.value
        entity.userName = domain.userName.value
        entity.userAvatarUrl = domain.userAvatarUrl.value
        entity.content = domain.content
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.createdAt // 생성일을 업데이트일로 초기 설정
        return entity
    }
    
    fun toDomain(entity: StoryCommentEntity): StoryComment {
        return StoryComment(
            id = entity.id,
            storyId = StoryId(entity.storyId),
            userId = UserId(entity.userId),
            userName = UserName(entity.userName),
            userAvatarUrl = AvatarUrl(entity.userAvatarUrl),
            content = entity.content,
            createdAt = entity.createdAt
        )
    }
}