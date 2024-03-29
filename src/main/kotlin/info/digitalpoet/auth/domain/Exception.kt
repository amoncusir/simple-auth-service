package info.digitalpoet.auth.domain

open class DomainException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

open class InvalidAuthentication(message: String? = null, cause: Throwable? = null): DomainException(message, cause)

class InvalidPassword(message: String? = null, cause: Throwable? = null) : InvalidAuthentication(message, cause)

class InvalidUser(message: String? = null, cause: Throwable? = null) : InvalidAuthentication(message, cause)

class InvalidRefreshId(message: String? = null, cause: Throwable? = null) : InvalidAuthentication(message, cause)

class InvalidPolicies(message: String? = null, cause: Throwable? = null) : InvalidAuthentication(message, cause)
