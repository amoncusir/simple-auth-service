package info.digitalpoet.auth.domain.service

class InvalidPassword(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidUser(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
