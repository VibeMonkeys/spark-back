package com.monkeys.spark.domain.service

import com.monkeys.spark.domain.model.User

/**
 * 사용자 비밀번호 관련 순수 도메인 서비스
 * 실제 암호화는 Infrastructure 레이어에서 처리하고, 
 * 여기서는 비즈니스 규칙만 담당
 */
class UserPasswordDomainService {
    
    /**
     * 비밀번호 변경 가능 여부 검증
     */
    fun canChangePassword(user: User, currentPassword: String): Boolean {
        // 실제 환경에서는 해시된 비밀번호와 비교
        // 지금은 단순 비교로 구현 (추후 Infrastructure 레이어에서 암호화 처리)
        return user.password == currentPassword
    }
    
    /**
     * 새 비밀번호 유효성 검증
     */
    fun validateNewPassword(newPassword: String): Boolean {
        // 비밀번호 정책 검증 (최소 길이, 복잡도 등)
        return newPassword.length >= 8
    }
    
    /**
     * 비밀번호 변경 처리
     */
    fun changePassword(user: User, currentPassword: String, newPassword: String): User {
        require(canChangePassword(user, currentPassword)) {
            "Current password is invalid"
        }
        
        require(validateNewPassword(newPassword)) {
            "New password does not meet security requirements"
        }
        
        // 도메인 모델을 통한 비밀번호 변경
        user.changePassword(newPassword)
        return user
    }
}