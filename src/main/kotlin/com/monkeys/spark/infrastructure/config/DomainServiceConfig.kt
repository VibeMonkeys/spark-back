package com.monkeys.spark.infrastructure.config

import com.monkeys.spark.domain.factory.MissionFactory
import com.monkeys.spark.domain.service.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 도메인 서비스들을 스프링 빈으로 등록하는 설정
 * 순수한 도메인 서비스들을 외부에서 주입받을 수 있도록 함
 */
@Configuration
class DomainServiceConfig {

    @Bean
    fun userMissionDomainService(): UserMissionDomainService {
        return UserMissionDomainService()
    }

    @Bean
    fun userPasswordDomainService(): UserPasswordDomainService {
        return UserPasswordDomainService()
    }

    @Bean
    fun storyMissionDomainService(): StoryMissionDomainService {
        return StoryMissionDomainService()
    }

    @Bean
    fun userLevelDomainService(): UserLevelDomainService {
        return UserLevelDomainService()
    }

    @Bean
    fun rewardDomainService(): RewardDomainService {
        return RewardDomainService()
    }

    @Bean
    fun achievementDomainService(): AchievementDomainService {
        return AchievementDomainService()
    }

    @Bean
    fun missionFactory(): MissionFactory {
        return MissionFactory()
    }
}