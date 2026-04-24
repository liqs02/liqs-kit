package com.patryklikus.kit.spring.exception

/**
 * Request is well-formed but the domain is in a state that forbids it
 * (e.g. transitioning a finalized order).
 */
class IllegalState(message: String) : ApiError(ErrorType.IllegalState, message)
