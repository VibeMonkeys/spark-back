package com.monkeys.spark.infrastructure.adapter.`in`.web.exception

import com.monkeys.spark.domain.exception.*
import com.monkeys.spark.infrastructure.adapter.`in`.web.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        ex: EntityNotFoundException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Entity not found: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "Entity not found", ex.errorCode)
        )
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        ex: UserNotFoundException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("User not found: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "User not found", ex.errorCode)
        )
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(
        ex: UserAlreadyExistsException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("User already exists: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "User already exists", ex.errorCode)
        )
    }

    @ExceptionHandler(MissionNotFoundException::class)
    fun handleMissionNotFoundException(
        ex: MissionNotFoundException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Mission not found: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "Mission not found", ex.errorCode)
        )
    }

    @ExceptionHandler(MissionNotAssignedException::class)
    fun handleMissionNotAssignedException(
        ex: MissionNotAssignedException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Mission not assigned: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "Mission not assigned", ex.errorCode)
        )
    }

    @ExceptionHandler(MissionAlreadyCompletedException::class)
    fun handleMissionAlreadyCompletedException(
        ex: MissionAlreadyCompletedException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Mission already completed: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "Mission already completed", ex.errorCode)
        )
    }

    @ExceptionHandler(DailyLimitExceededException::class)
    fun handleDailyLimitExceededException(
        ex: DailyLimitExceededException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Daily limit exceeded: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "Daily limit exceeded", ex.errorCode)
        )
    }

    @ExceptionHandler(MissionInProgressException::class)
    fun handleMissionInProgressException(
        ex: MissionInProgressException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Mission in progress: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "Mission already in progress", ex.errorCode)
        )
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(
        ex: InvalidCredentialsException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Invalid credentials: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>("로그인 정보가 올바르지 않습니다.", ex.errorCode)
        )
    }

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPasswordException(
        ex: InvalidPasswordException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Invalid password: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>("현재 비밀번호가 올바르지 않습니다.", ex.errorCode)
        )
    }

    @ExceptionHandler(InsufficientPointsException::class)
    fun handleInsufficientPointsException(
        ex: InsufficientPointsException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Insufficient points: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>("포인트가 부족합니다.", ex.errorCode)
        )
    }

    @ExceptionHandler(BusinessRuleViolationException::class)
    fun handleBusinessRuleViolationException(
        ex: BusinessRuleViolationException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Business rule violation: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "비즈니스 규칙 위반", ex.errorCode)
        )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(
        ex: ValidationException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Validation error: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "입력값이 올바르지 않습니다.", ex.errorCode)
        )
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("Domain exception: ${ex.message}")
        return ResponseEntity.ok(
            ApiResponse.error<Nothing>(ex.message ?: "도메인 오류가 발생했습니다.", ex.errorCode)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.error<Nothing>("내부 서버 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR")
        )
    }
}