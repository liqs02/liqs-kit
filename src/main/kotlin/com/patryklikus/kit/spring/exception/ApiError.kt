package com.patryklikus.kit.spring.exception

abstract class ApiError(val type: ErrorType, message: String = "") : RuntimeException(message)
