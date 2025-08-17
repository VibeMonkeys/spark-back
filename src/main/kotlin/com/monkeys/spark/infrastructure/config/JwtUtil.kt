package com.monkeys.spark.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*

@Component  
class JwtUtil {
    
    @Value("\${jwt.secret:mySecretKeyForJwtTokenGenerationThatIsLongEnough}")
    private lateinit var secret: String
    
    @Value("\${jwt.access-token-validity:86400}") // 24 hours in seconds
    private var accessTokenValidity: Long = 86400
    
    @Value("\${jwt.refresh-token-validity:604800}") // 7 days in seconds  
    private var refreshTokenValidity: Long = 604800
    
    fun generateAccessToken(userId: String): String {
        val now = System.currentTimeMillis()
        val expiryTime = now + (accessTokenValidity * 1000)
        val payload = "$userId:access:$expiryTime"
        val signature = createSignature(payload)
        return Base64.getEncoder().encodeToString("$payload:$signature".toByteArray())
    }
    
    fun generateRefreshToken(userId: String): String {
        val now = System.currentTimeMillis()
        val expiryTime = now + (refreshTokenValidity * 1000)
        val payload = "$userId:refresh:$expiryTime"
        val signature = createSignature(payload)
        return Base64.getEncoder().encodeToString("$payload:$signature".toByteArray())
    }
    
    fun getUserIdFromToken(token: String): String? {
        return try {
            val decoded = String(Base64.getDecoder().decode(token))
            val parts = decoded.split(":")
            if (parts.size >= 4 && validateSignature(parts.take(3).joinToString(":"), parts[3])) {
                parts[0]
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    fun getTokenType(token: String): String? {
        return try {
            val decoded = String(Base64.getDecoder().decode(token))
            val parts = decoded.split(":")
            if (parts.size >= 4 && validateSignature(parts.take(3).joinToString(":"), parts[3])) {
                parts[1]
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    fun isTokenExpired(token: String): Boolean {
        return try {
            val decoded = String(Base64.getDecoder().decode(token))
            val parts = decoded.split(":")
            if (parts.size >= 4 && validateSignature(parts.take(3).joinToString(":"), parts[3])) {
                val expiryTime = parts[2].toLong()
                System.currentTimeMillis() > expiryTime
            } else true
        } catch (e: Exception) {
            true
        }
    }
    
    fun validateToken(token: String, userId: String): Boolean {
        val tokenUserId = getUserIdFromToken(token)
        return (tokenUserId == userId && !isTokenExpired(token))
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token) && getUserIdFromToken(token) != null
        } catch (e: Exception) {
            false
        }
    }
    
    fun validateAccessToken(token: String): Boolean {
        return try {
            val tokenType = getTokenType(token)
            tokenType == "access" && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
    
    fun validateRefreshToken(token: String): Boolean {
        return try {
            val tokenType = getTokenType(token)
            tokenType == "refresh" && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun createSignature(payload: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = payload + secret
        val hash = md.digest(combined.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
    
    private fun validateSignature(payload: String, signature: String): Boolean {
        return createSignature(payload) == signature
    }
}