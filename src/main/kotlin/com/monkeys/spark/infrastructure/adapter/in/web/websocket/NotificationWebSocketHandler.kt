package com.monkeys.spark.infrastructure.adapter.`in`.web.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.monkeys.spark.infrastructure.config.JwtUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

@Component
class NotificationWebSocketHandler(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    private val logger = LoggerFactory.getLogger(NotificationWebSocketHandler::class.java)
    
    // userId -> WebSocketSession 매핑
    private val userSessions = ConcurrentHashMap<Long, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("WebSocket connection established: ${session.id}")
        
        // URL에서 JWT 토큰 추출
        val token = extractTokenFromQuery(session)
        logger.info("Extracted token from WebSocket: ${token?.take(10)}...")
        
        // 테스트용 더미 토큰 처리 (dummy_token_숫자 형태)
        if (token != null && token.startsWith("dummy_token_")) {
            val userId = token.substringAfter("dummy_token_").toLongOrNull()
            if (userId != null) {
                userSessions[userId] = session
                session.attributes["userId"] = userId
                logger.info("User $userId connected via WebSocket (test mode)")
            } else {
                logger.warn("Invalid test user ID format for WebSocket connection: ${session.id}")
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid user ID"))
            }
        } else if (token != null) {
            logger.debug("Validating JWT token for WebSocket connection")
            val isValid = jwtUtil.validateToken(token)
            logger.debug("Token validation result: $isValid")
            
            if (isValid) {
                val userIdString = jwtUtil.getUserIdFromToken(token)
                logger.debug("Extracted user ID from token: $userIdString")
                
                if (userIdString != null) {
                    val userId = userIdString.toLongOrNull()
                    if (userId != null) {
                        userSessions[userId] = session
                        session.attributes["userId"] = userId
                        logger.info("User $userId connected via WebSocket")
                    } else {
                        logger.warn("Invalid user ID format for WebSocket connection: ${session.id}, userIdString: $userIdString")
                        session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid user ID"))
                    }
                } else {
                    logger.warn("Could not extract user ID from token for WebSocket connection: ${session.id}")
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"))
                }
            } else {
                logger.warn("Invalid token for WebSocket connection: ${session.id}")
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"))
            }
        } else {
            logger.warn("No token provided for WebSocket connection: ${session.id}")
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No token provided"))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = session.attributes["userId"] as? Long
        if (userId != null) {
            userSessions.remove(userId)
            logger.info("User $userId disconnected from WebSocket")
        }
        logger.info("WebSocket connection closed: ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // 클라이언트로부터 메시지 처리 (예: 알림 읽음 확인)
        try {
            val payload = objectMapper.readValue(message.payload, Map::class.java)
            val type = payload["type"] as? String
            
            when (type) {
                "ping" -> {
                    // 연결 유지를 위한 ping 메시지
                    session.sendMessage(TextMessage("""{"type":"pong"}"""))
                }
                "markAsRead" -> {
                    // 알림 읽음 처리는 REST API를 통해 처리
                    logger.info("Mark as read request from ${session.attributes["userId"]}")
                }
            }
        } catch (e: Exception) {
            logger.error("Error handling WebSocket message: ${e.message}")
        }
    }

    fun sendNotificationToUser(userId: Long, notification: Any) {
        val session = userSessions[userId]
        if (session != null && session.isOpen) {
            try {
                val message = objectMapper.writeValueAsString(mapOf(
                    "type" to "notification",
                    "data" to notification
                ))
                session.sendMessage(TextMessage(message))
                logger.info("Sent notification to user $userId")
            } catch (e: Exception) {
                logger.error("Error sending notification to user $userId: ${e.message}")
                userSessions.remove(userId)
            }
        } else {
            logger.debug("User $userId not connected to WebSocket")
        }
    }

    fun getConnectedUserCount(): Int = userSessions.size

    fun isUserConnected(userId: Long): Boolean = userSessions.containsKey(userId)

    private fun extractTokenFromQuery(session: WebSocketSession): String? {
        val query = session.uri?.query
        logger.info("WebSocket query string: $query")
        
        if (query == null) {
            logger.info("No query string found in WebSocket URL")
            return null
        }
        
        val tokenValue = query.split("&")
            .map { it.split("=", limit = 2) }
            .firstOrNull { it.size == 2 && it[0] == "token" }
            ?.get(1)
        
        logger.info("Raw token value extracted: $tokenValue")
        
        return if (tokenValue != null) {
            try {
                val decodedToken = URLDecoder.decode(tokenValue, StandardCharsets.UTF_8)
                logger.info("Decoded token: ${decodedToken.take(10)}...")
                decodedToken
            } catch (e: Exception) {
                logger.warn("Failed to decode token: ${e.message}")
                tokenValue
            }
        } else {
            logger.info("No token parameter found in query string")
            null
        }
    }
}