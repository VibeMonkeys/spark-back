package com.monkeys.spark.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.http.HttpMethod

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customUserDetailsService: CustomUserDetailsService
) {

    @Value("\${cors.allowed-origins:http://localhost:3000,http://localhost:3001,http://localhost:3002,http://localhost:5173}")
    private lateinit var allowedOrigins: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .csrf { csrf ->
                csrf.disable()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints - no authentication required
                    .requestMatchers(
                        "/api/v1/auth/**",
                        "/api/health",
                        "/error",
                        "/actuator/**"
                    ).permitAll()
                    
                    // Public mission endpoints (read-only)
                    .requestMatchers(
                        HttpMethod.GET, "/api/v1/missions/**"
                    ).permitAll()
                    
                    // Public user endpoints (read-only) 
                    .requestMatchers(
                        HttpMethod.GET, "/api/v1/users/**"
                    ).permitAll()
                    
                    // Public story endpoints (read-only)
                    .requestMatchers(
                        HttpMethod.GET, "/api/v1/stories/feed"
                    ).permitAll()
                    
                    // Public level endpoints (read-only) - for level system display
                    .requestMatchers(
                        HttpMethod.GET, "/api/v1/levels/**"
                    ).permitAll()
                    
                    // Temporarily allow notification endpoints for testing
                    .requestMatchers(
                        "/api/v1/notifications/**"
                    ).permitAll()
                    
                    // Temporarily allow mission operations for testing
                    .requestMatchers(
                        "/api/v1/missions/**"
                    ).permitAll()
                    
                    // Allow WebSocket connections
                    .requestMatchers(
                        "/ws/**"
                    ).permitAll()
                    
                    // Temporarily allow profile updates for testing
                    .requestMatchers(
                        HttpMethod.PUT, "/api/v1/users/*/profile"
                    ).permitAll()
                    .requestMatchers(
                        HttpMethod.POST, "/api/v1/users/*/change-password"
                    ).permitAll()

                    // Protected endpoints - authentication required
                    .requestMatchers("/api/v1/missions/**").authenticated()  // POST, PUT, DELETE missions
                    .requestMatchers("/api/v1/users/**").authenticated()     // POST, PUT, DELETE users
                    .requestMatchers("/api/v1/stories/**").authenticated()   // All story operations
                    .requestMatchers("/api/v1/stats/**").authenticated()     // All stats operations
                    .requestMatchers("/api/v1/levels/**").authenticated()    // All level operations
                    .requestMatchers("/api/inquiries/**").authenticated()    // All inquiry operations
                    
                    // All other endpoints require authentication
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        // 환경변수에서 허용된 오리진들을 읽어와서 설정
        val origins = allowedOrigins.split(",").map { it.trim() }

        val configuration = CorsConfiguration().apply {
            // 환경변수로 설정된 origins 사용
            allowedOrigins = origins

            // Allow specific HTTP methods
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

            // Allow specific headers
            allowedHeaders = listOf("*")

            // Allow credentials (cookies, authorization headers, etc.)
            allowCredentials = true

            // Cache preflight response for 1 hour
            maxAge = 3600L
        }


        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

}