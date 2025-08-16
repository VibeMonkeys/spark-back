package com.monkeys.spark.domain.exception

/**
 * 공통 예외들
 */

class InvalidStoryContentException(message: String) : DomainException(
    message,
    "INVALID_STORY_CONTENT"
)

/**
 * 문의 관련 예외
 */
class InquiryNotFoundException(inquiryId: String) : EntityNotFoundException("Inquiry", inquiryId, "INQUIRY_NOT_FOUND")

/**
 * 비즈니스 규칙 위반 예외
 */
class BusinessRuleViolationException(message: String, errorCode: String = "BUSINESS_RULE_VIOLATION") : DomainException(
    message,
    errorCode
)

/**
 * 검증 관련 예외
 */
class ValidationException(message: String, errorCode: String = "VALIDATION_ERROR") : DomainException(
    message,
    errorCode
)