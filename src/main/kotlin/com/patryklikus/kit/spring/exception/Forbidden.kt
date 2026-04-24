package com.patryklikus.kit.spring.exception

/** Caller is authenticated but lacks permission for the requested action. */
class Forbidden : ApiError(ErrorType.Forbidden)
