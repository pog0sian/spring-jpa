package com.example.springjpa.api.exception

import com.example.springjpa.api.dto.ErrorResponse
import com.example.springjpa.application.exception.NotFoundException
import com.example.springjpa.application.exception.RestaurantNotFoundException
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException::class)
    fun handleRestaurantNotFound(e: RestaurantNotFoundException): ResponseEntity<ErrorResponse> {

        val error = ErrorResponse(
            status = 404,
            error = "Not Found",
            message = e.message ?: "Restaurant not found"
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFound(e: NotFoundException): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.NOT_FOUND
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, e.message ?: "Not Found")
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: Exception): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, e.message ?: "Bad Request")
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, "Некорректные данные")
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun notReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, "Некорректные данные")
        )
    }

    @ExceptionHandler(Exception::class)
    fun generic(e: Exception): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, "Внутренняя ошибка сервера")
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun noSuchElement(e: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, e.message ?: "Bad Request")
        )
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException::class)
    fun invalidDataAccess(e: InvalidDataAccessApiUsageException): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity.status(status).body(
            ErrorResponse(status.value(), status.reasonPhrase, e.message ?: "Bad Request")
        )
    }

}