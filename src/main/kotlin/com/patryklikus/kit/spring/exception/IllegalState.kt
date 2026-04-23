package com.patryklikus.kit.spring.exception

class IllegalState(message: String) : ApiError(ErrorType.IllegalState, message)
