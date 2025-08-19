package com.monkeys.spark.infrastructure.config

import com.monkeys.spark.domain.service.DailyQuestRewardDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 일일 퀘스트 시스템 Bean 설정
 * "삶을 게임처럼 즐겨라!" - 순수 도메인 서비스들을 Spring Bean으로 등록
 */
@Configuration
class DailyQuestConfig {
    
    /**
     * 일일 퀘스트 보상 도메인 서비스 Bean 등록
     */
    @Bean
    fun dailyQuestRewardDomainService(): DailyQuestRewardDomainService {
        return DailyQuestRewardDomainService()
    }
}