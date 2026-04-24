package com.patryklikus.kit.spring.exception

import org.springframework.http.HttpStatus

enum class ErrorType(val httpStatus: HttpStatus) {
    /** Request payload / parameters failed validation or couldn't be parsed. */
    InvalidData(HttpStatus.BAD_REQUEST),

    /** Request is syntactically valid but rejected due to the current domain state. */
    IllegalState(HttpStatus.BAD_REQUEST),

    /** A referenced associated entity does not exist. */
    NoAssociation(HttpStatus.BAD_REQUEST),

    /** Caller is not authenticated. */
    Unauthorized(HttpStatus.UNAUTHORIZED),

    /** Caller is authenticated but lacks permission. */
    Forbidden(HttpStatus.FORBIDDEN),

    /** Target entity does not exist. */
    NotFound(HttpStatus.NOT_FOUND),

    /** Request conflicts with existing state (e.g. unique constraint violation). */
    Conflict(HttpStatus.CONFLICT),

    /** Unexpected server-side failure. */
    InternalServerError(HttpStatus.INTERNAL_SERVER_ERROR),
}
