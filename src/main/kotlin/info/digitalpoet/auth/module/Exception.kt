package info.digitalpoet.auth.module

import kotlin.reflect.KClass

open class DIException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidProvider(contract: KClass<*>, provider: String, cause: Throwable? = null):
    DIException("Invalid provider [${provider}] for contract [${contract.qualifiedName}]", cause)
