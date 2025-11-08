package com.muratcant.audiolog.domain.common

sealed class DomainError(message: String) : Exception(message) {
    class NotFound(message: String) : DomainError(message)
    class ValidationError(message: String) : DomainError(message)
    class Conflict(message: String) : DomainError(message)
    class ExternalServiceError(message: String, cause: Throwable? = null) : DomainError(message) {
        init {
            cause?.let { initCause(it) }
        }
    }
}

