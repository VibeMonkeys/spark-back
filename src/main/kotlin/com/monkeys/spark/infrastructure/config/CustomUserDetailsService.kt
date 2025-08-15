package com.monkeys.spark.infrastructure.config

import com.monkeys.spark.infrastructure.adapter.out.persistence.repository.UserJpaRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userJpaRepository: UserJpaRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val userEntity = userJpaRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        // 비밀번호가 비어있는 경우 로그 출력
        if (userEntity.password.isBlank()) {
            throw UsernameNotFoundException("User password is invalid: $email")
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return User.builder()
            .username(userEntity.id) // Use user ID as username for JWT
            .password(userEntity.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }

    fun loadUserById(userId: String): UserDetails? {
        val userEntity = userJpaRepository.findById(userId).orElse(null)
            ?: return null

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return User.builder()
            .username(userEntity.id)
            .password(userEntity.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}