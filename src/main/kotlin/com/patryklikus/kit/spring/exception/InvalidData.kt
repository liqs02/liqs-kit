package com.patryklikus.kit.spring.exception

/** Request payload or parameters are invalid beyond what Bean Validation catches. */
class InvalidData(message: String) : ApiError(ErrorType.InvalidData, message)
