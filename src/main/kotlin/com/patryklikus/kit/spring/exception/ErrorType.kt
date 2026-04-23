package com.patryklikus.kit.spring.exception

import org.springframework.http.HttpStatus

enum class ErrorType(val httpStatus: HttpStatus) {
    InvalidData(HttpStatus.BAD_REQUEST),
    IllegalState(HttpStatus.BAD_REQUEST),
    NoAssociation(HttpStatus.BAD_REQUEST),
    Unauthorized(HttpStatus.UNAUTHORIZED),
    Forbidden(HttpStatus.FORBIDDEN),
    NotFound(HttpStatus.NOT_FOUND),
    Conflict(HttpStatus.CONFLICT),
    InternalServerError(HttpStatus.INTERNAL_SERVER_ERROR),
}
