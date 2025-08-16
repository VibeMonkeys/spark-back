package com.monkeys.spark.domain.exception

/**
 * 사용자 관련 예외들
 */

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 */
class UserNotFoundException(userId: String) : EntityNotFoundException("User", userId, "USER_NOT_FOUND")

/**
 * 이미 존재하는 사용자 예외
 */
class UserAlreadyExistsException(email: String) : DomainException(
    "User with email '$email' already exists",
    "USER_ALREADY_EXISTS"
)

/**
 * 인증 관련 예외
 */
class InvalidCredentialsException(message: String = "Invalid credentials") : DomainException(
    message,
    "INVALID_CREDENTIALS"
)

// InvalidPasswordException moved to UserPasswordExceptions.kt