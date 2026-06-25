package com.schoolfinder.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.OffsetDateTime

class NotFoundException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String = "Authentication required") : RuntimeException(message)

data class ApiError(
    val status: Int,
    val error: String,
    val message: String?,
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun notFound(ex: NotFoundException) = build(HttpStatus.NOT_FOUND, ex.message)

    @ExceptionHandler(BadRequestException::class)
    fun badRequest(ex: BadRequestException) = build(HttpStatus.BAD_REQUEST, ex.message)

    @ExceptionHandler(ConflictException::class)
    fun conflict(ex: ConflictException) = build(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(ex: UnauthorizedException) = build(HttpStatus.UNAUTHORIZED, ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val msg = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return build(HttpStatus.BAD_REQUEST, msg)
    }

    private fun build(status: HttpStatus, message: String?): ResponseEntity<ApiError> =
        ResponseEntity.status(status).body(ApiError(status.value(), status.reasonPhrase, message))
}
