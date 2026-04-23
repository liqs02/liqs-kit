package com.patryklikus.kit.spring.exception

import jakarta.persistence.Entity
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApiError::class)
    fun handleApiError(e: ApiError): ResponseEntity<ErrorResponse> {
        if (e.type == ErrorType.InternalServerError) {
            log.error("ApiError", e)
        }
        return response(e.type, e.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val first = e.bindingResult.fieldErrors.firstOrNull()
        val message = first?.let { "${it.field} ${it.defaultMessage}" }
        return response(ErrorType.InvalidData, message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> =
        response(ErrorType.InvalidData, null)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        response(ErrorType.InvalidData, "Malformed request body")

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> =
        response(ErrorType.InvalidData, "Invalid value for parameter '${e.name}'")

    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariable(e: MissingPathVariableException): ResponseEntity<ErrorResponse> {
        val paramType = e.parameter.parameterType
        return if (paramType.isAnnotationPresent(Entity::class.java)) {
            response(ErrorType.NotFound, "${paramType.simpleName.lowercase()} not found")
        } else {
            log.error("Missing path variable", e)
            response(ErrorType.InternalServerError, null)
        }
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ErrorResponse> =
        response(ErrorType.Forbidden, null)

    @ExceptionHandler(ErrorResponseException::class, NoResourceFoundException::class)
    fun handleErrorResponse(e: Exception): ResponseEntity<Void> {
        val status = when (e) {
            is ErrorResponseException -> e.statusCode
            is NoResourceFoundException -> e.statusCode
            else -> org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity.status(status).build()
    }

    @ExceptionHandler(Exception::class)
    fun handleFallback(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", e)
        return response(ErrorType.InternalServerError, null)
    }

    private fun response(type: ErrorType, message: String?): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(type, message?.takeIf { it.isNotEmpty() })
        return ResponseEntity.status(type.httpStatus).body(body)
    }
}
