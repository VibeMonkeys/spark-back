package com.monkeys.spark.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    @Value("\${cors.allowed-origins:http://localhost:3001,http://localhost:3002,http://localhost:5173}")
    private lateinit var allowedOrigins: String

    override fun addCorsMappings(registry: CorsRegistry) {
        // 환경변수에서 허용된 오리진들을 읽어와서 설정
        val origins = allowedOrigins.split(",").map { it.trim() }.toTypedArray()
        
        registry.addMapping("/api/**")
            .allowedOrigins(*origins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
        
        println("🌐 [CORS] Configured allowed origins: ${origins.joinToString(", ")}")
    }
}