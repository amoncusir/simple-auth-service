package info.digitalpoet.auth.application.rest

open class RestException(val code: Int, message: String?, cause: Throwable?): RuntimeException(message, cause)

open class UnauthorizedPetition(cause: Throwable? = null): RestException(403, "Unauthorized Access", cause)
