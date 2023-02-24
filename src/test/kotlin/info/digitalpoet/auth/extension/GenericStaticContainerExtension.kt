package info.digitalpoet.auth.extension

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource
import org.junit.platform.commons.util.AnnotationUtils
import org.testcontainers.containers.GenericContainer
import java.lang.annotation.Inherited
import java.lang.reflect.AnnotatedElement
import java.util.Optional
import java.util.logging.Logger

/** To use in Annotation composed mode
 * @see org.junit.jupiter.api.extension.ExtendWith
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class GenericStaticContainerExtension<C: GenericContainer<*>>(
    protected val defaultCleanUp: Lifecycle.CleanUp = Lifecycle.CleanUp.BEFORE_CLASS
):
    BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback
{
    @Inherited
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class Lifecycle(
        vararg val cleanup: CleanUp = []
    ) {
        enum class CleanUp {
            BEFORE_CLASS, BEFORE_METHOD, AFTER_METHOD, AFTER_CLASS, NEVER
        }
    }

    inner class ClosableContainer(val container: C): CloseableResource
    {
        fun start(context: ExtensionContext) {
            logger.info { "Start Container Lifecycle" }

            container.start()
            startContainer(container, context)
            setUpContainer(container, context)
        }

        fun clean(context: ExtensionContext) {
            logger.info { "Clean Container Lifecycle" }

            restoreContainer(container, context)
        }

        override fun close() {
            logger.info { "Close Container Lifecycle" }

            cleanUpContainer(container, null)
            closeContainer(container)
            container.close()
        }

        override fun toString() = container.toString()
    }

    companion object {
        const val CONTAINER_KEY = "container"
    }

    protected val namespace: Namespace by lazy { Namespace.create(this::class) }

    protected val logger by lazy { Logger.getLogger("${this::class.simpleName}[${System.identityHashCode(this)}]") }

    protected val containerKey: String
        get() = this::class.qualifiedName + "::" + CONTAINER_KEY

    abstract fun buildContainer(): C

    open fun startContainer(container: C, context: ExtensionContext) {}

    open fun setUpContainer(container: C, context: ExtensionContext) {}

    open fun cleanUpContainer(container: C, context: ExtensionContext?) {}

    open fun restoreContainer(container: C, context: ExtensionContext) {
        cleanUpContainer(container, context)
        setUpContainer(container, context)
    }

    open fun closeContainer(container: C) {}

    open fun createClosableContainer(context: ExtensionContext): ClosableContainer {

        val container = buildContainer().let(::ClosableContainer)

        logger.info { "Create new Container :: $container" }

        container.start(context)

        return container
    }

    override fun beforeAll(context: ExtensionContext)
    {
        var firstRun = false
        val container = context.store.getOrComputeIfAbsent(
            containerKey, { createClosableContainer(context).apply { firstRun = true } }, ClosableContainer::class.java
        )

        if (!firstRun)
        {
            val lifecycleClenUp = context.lifecycleClenUp

            logger.info { "::beforeAll - lifecycleClenUp=${lifecycleClenUp.joinToString(", ")}, container=$container" }

            if (lifecycleClenUp.apply(Lifecycle.CleanUp.BEFORE_CLASS))
            {
                container.clean(context)
            }
        }
    }

    override fun beforeEach(context: ExtensionContext)
    {
        val container = context.container
        val lifecycleClenUp = context.lifecycleClenUp

        logger.info { "::beforeEach - lifecycleClenUp=${lifecycleClenUp.joinToString(", ")}, container=$container" }

        if(lifecycleClenUp.apply(Lifecycle.CleanUp.BEFORE_METHOD)) {
            container.clean(context)
        }
    }

    override fun afterEach(context: ExtensionContext)
    {
        val container = context.container
        val lifecycleClenUp = context.lifecycleClenUp

        logger.info { "::afterEach - lifecycleClenUp=${lifecycleClenUp.joinToString(", ")}, container=$container" }

        if(lifecycleClenUp.apply(Lifecycle.CleanUp.AFTER_METHOD)) {
            container.clean(context)
        }
    }

    override fun afterAll(context: ExtensionContext)
    {
        val container = context.container
        val lifecycleClenUp = context.lifecycleClenUp

        logger.info { "::afterAll - lifecycleClenUp=${lifecycleClenUp.joinToString(", ")}, container=$container" }

        if(lifecycleClenUp.apply(Lifecycle.CleanUp.AFTER_CLASS)) {
            container.clean(context)
        }
    }

    protected val ExtensionContext.store: Store
        get() = root.getStore(namespace)

    protected val ExtensionContext.container: GenericStaticContainerExtension<*>.ClosableContainer
        get() = store.get(containerKey)!! as GenericStaticContainerExtension<*>.ClosableContainer

    private val ExtensionContext.lifecycleClenUp: Array<out Lifecycle.CleanUp>
        get() = testClass
            .lifecycleClenUp
            .or { testClass.map { it?.enclosingClass ?: it }.lifecycleClenUp }
            .orElse(arrayOf(defaultCleanUp))

    private val Optional<out AnnotatedElement>.lifecycleClenUp
        get() = flatMap { AnnotationUtils.findAnnotation(it, Lifecycle::class.java) }
            .map { it.cleanup }

    private fun Array<out Lifecycle.CleanUp>.apply(option: Lifecycle.CleanUp) = any { it == option }
}
