package info.digitalpoet.auth.domain.service

open class DomainException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidPassword(message: String? = null, cause: Throwable? = null) : DomainException(message, cause)

class InvalidUser(message: String? = null, cause: Throwable? = null) : DomainException(message, cause)
