package com.monkeys.spark.domain.exception

/**
 * 도메인 레이어의 기본 예외
 */
abstract class DomainException(
    message: String,
    val errorCode: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 엔티티를 찾을 수 없을 때 발생하는 예외
 */
open class EntityNotFoundException(
    entityName: String,
    id: String,
    errorCode: String = "ENTITY_NOT_FOUND"
) : DomainException("$entityName with id '$id' not found", errorCode)