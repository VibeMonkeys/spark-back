package com.monkeys.spark.infrastructure.config

import com.monkeys.spark.infrastructure.adapter.`in`.web.websocket.NotificationWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val notificationWebSocketHandler: NotificationWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
            .setAllowedOrigins("http://localhost:3000", "http://localhost:3002", "http://localhost:5173", "https://spark-front.vercel.app") // 프론트엔드 도메인들
    }
}