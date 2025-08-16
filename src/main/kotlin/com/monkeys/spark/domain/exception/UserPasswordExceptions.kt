package com.monkeys.spark.domain.exception

class InvalidPasswordException(message: String = "Invalid password") : DomainException(
    message,
    "INVALID_PASSWORD"
)

class WeakPasswordException(message: String = "Password does not meet security requirements") : DomainException(
    message,
    "WEAK_PASSWORD"
)