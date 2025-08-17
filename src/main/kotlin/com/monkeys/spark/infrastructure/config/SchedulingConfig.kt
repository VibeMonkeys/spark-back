package com.monkeys.spark.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * 스케줄링 설정
 * 자동 미션 만료, 알림 정리 등의 배치 작업을 위한 설정
 */
@Configuration
@EnableScheduling
class SchedulingConfig