package info.digitalpoet.auth.domain.repository

open class RepositoryException(message: String?, cause: Throwable?): RuntimeException(message, cause)

class NotFoundEntity(entityId: String, entityName: String, cause: Throwable? = null):
    RepositoryException("Can't found the ID $entityId on $entityName", cause)

class InvalidAuthentication(message: String?, cause: Throwable? = null): RepositoryException(message, cause)
