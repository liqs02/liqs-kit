package com.patryklikus.kit.spring.exception

import jakarta.persistence.Entity
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    /** Application-thrown [ApiError]. Logs at `error` only when [ErrorType.InternalServerError]. */
    @ExceptionHandler(ApiError::class)
    fun handleApiError(e: ApiError): ResponseEntity<ErrorResponse> {
        if (e.type == ErrorType.InternalServerError) {
            log.error("ApiError", e)
        }
        return response(e.type, e.message ?: "")
    }

    /** `@Valid` failure on a `@RequestBody`; message carries the first field error. */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val first = e.bindingResult.fieldErrors.firstOrNull()
        val message = first?.let { "${it.field} ${it.defaultMessage}" } ?: ""
        return response(ErrorType.InvalidData, message)
    }

    /** Bean Validation failure on method parameters (e.g. `@RequestParam` with `@Min`). */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> =
        response(ErrorType.InvalidData, "")

    /** Request body is missing or malformed JSON. */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        response(ErrorType.InvalidData, "Malformed request body")

    /** Path / query parameter cannot be converted to the declared type. */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> =
        response(ErrorType.InvalidData, "Invalid value for parameter '${e.name}'")

    /** A required `@PathVariable` has no value in the request URI. */
    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariable(e: MissingPathVariableException): ResponseEntity<ErrorResponse> {
        val paramType = e.parameter.parameterType
        return if (paramType.isAnnotationPresent(Entity::class.java)) {
            response(ErrorType.NotFound, "${paramType.simpleName.lowercase()} not found")
        } else {
            log.error("Missing path variable", e)
            response(ErrorType.InternalServerError, "")
        }
    }

    /** Spring Security authorization failure. */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ErrorResponse> =
        response(ErrorType.Forbidden, "")

    /**
     * Framework-native HTTP errors carrying their own status (e.g. `404` for unknown static resource) —
     * forwarded as empty-body responses to preserve Spring's default semantics.
     */
    @ExceptionHandler(ErrorResponseException::class, NoResourceFoundException::class)
    fun handleErrorResponse(e: Exception): ResponseEntity<Void> {
        val status = when (e) {
            is ErrorResponseException -> e.statusCode
            is NoResourceFoundException -> e.statusCode
            else -> org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity.status(status).build()
    }

    /** Last-resort catch-all for any unhandled exception. Logs at `error`. */
    @ExceptionHandler(Exception::class)
    fun handleFallback(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", e)
        return response(ErrorType.InternalServerError, "")
    }

    private fun response(type: ErrorType, message: String): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(type.httpStatus).body(ErrorResponse(type, message))
}
