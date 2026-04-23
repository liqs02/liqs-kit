package com.patryklikus.kit.spring.exception

class InvalidData(message: String) : ApiError(ErrorType.InvalidData, message)
