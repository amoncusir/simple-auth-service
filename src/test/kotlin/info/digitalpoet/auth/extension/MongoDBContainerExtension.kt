package info.digitalpoet.auth.extension

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

class MongoDBContainerExtension: GenericStaticContainerExtension<MongoDBContainer>()
{
    override fun buildContainer(): MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))

    override fun startContainer(container: MongoDBContainer, context: ExtensionContext) {
        System.setProperty("container.mongodb.url", container.getReplicaSetUrl("auth"))
    }

    override fun setUpContainer(container: MongoDBContainer, context: ExtensionContext) {

    }

    override fun cleanUpContainer(container: MongoDBContainer, context: ExtensionContext?) {

    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(MongoDBContainerExtension::class)
annotation class WithMongoDBContainer
