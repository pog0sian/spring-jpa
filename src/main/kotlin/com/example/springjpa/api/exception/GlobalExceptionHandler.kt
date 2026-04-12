package com.example.springjpa.api.exception

import com.example.springjpa.api.dto.ErrorResponse
import com.example.springjpa.api.dto.ValidationErrorResponse
import com.example.springjpa.application.exception.AlreadyExistsException
import com.example.springjpa.application.exception.AppException
import com.example.springjpa.application.exception.BadRequestException
import com.example.springjpa.application.exception.InvalidOrderStateException
import com.example.springjpa.application.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(AppException::class)
    fun handleCommon(e: AppException): ResponseEntity<ErrorResponse> {
        val status = when (e) {
            is NotFoundException -> HttpStatus.NOT_FOUND
            is AlreadyExistsException -> HttpStatus.CONFLICT
            is InvalidOrderStateException -> HttpStatus.BAD_REQUEST
            is BadRequestException -> HttpStatus.BAD_REQUEST
        }

        if (e is NotFoundException) {
            logger.warn { e.message ?: "Requested resource not found" }
        }

        return ResponseEntity
            .status(status)
            .body(ErrorResponse(status.value(), e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Incorrect value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Method parameter validation error",
                errors
            ))
    }
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ErrorResponse> {
        logger.warn { e.message ?: "Bad request" }
        return ResponseEntity
            .status(400)
            .body(ErrorResponse(400, e.message))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        logger.warn { "Authentication failed" }
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Неверный email или пароль"))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> {
        logger.warn { "Access denied" }
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(HttpStatus.FORBIDDEN.value(), "Доступ запрещён"))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBadJson(e: HttpMessageNotReadableException): ResponseEntity<ValidationErrorResponse> {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ValidationErrorResponse(
                    400,
                    "Malformed JSON or missing required fields",
                    emptyMap()
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error(e) { "Unexpected error" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "Internal server error"))
    }

}
