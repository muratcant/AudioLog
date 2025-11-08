package com.muratcant.audiolog.infrastructure.web

import com.muratcant.audiolog.domain.common.DomainError
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(DomainError.NotFound::class)
    fun handleNotFound(ex: DomainError.NotFound): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                error = "not_found",
                message = ex.message ?: "Resource not found",
                details = emptyMap()
            ))
    }
    
    @ExceptionHandler(DomainError.ValidationError::class)
    fun handleValidationError(ex: DomainError.ValidationError): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                error = "validation_error",
                message = ex.message ?: "Validation failed",
                details = emptyMap()
            ))
    }
    
    @ExceptionHandler(DomainError.Conflict::class)
    fun handleConflict(ex: DomainError.Conflict): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(
                error = "conflict",
                message = ex.message ?: "Resource conflict",
                details = emptyMap()
            ))
    }
    
    @ExceptionHandler(DomainError.ExternalServiceError::class)
    fun handleExternalServiceError(ex: DomainError.ExternalServiceError): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(ErrorResponse(
                error = "external_service_error",
                message = ex.message ?: "External service error",
                details = emptyMap()
            ))
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                error = "validation_error",
                message = ex.message ?: "Invalid argument",
                details = emptyMap()
            ))
    }
}

@Schema(description = "Hata yanıtı")
data class ErrorResponse(
    @Schema(description = "Hata kodu", example = "validation_error")
    val error: String,
    @Schema(description = "Hata mesajı", example = "Validation failed")
    val message: String,
    @Schema(description = "Hata detayları", example = "{\"field\": \"reason\"}")
    val details: Map<String, String>
)

