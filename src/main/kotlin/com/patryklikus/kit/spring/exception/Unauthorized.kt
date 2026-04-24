package com.patryklikus.kit.spring.exception

/** Caller is not authenticated (missing / invalid credentials). */
class Unauthorized : ApiError(ErrorType.Unauthorized)
