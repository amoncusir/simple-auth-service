package info.digitalpoet.auth.extension

import com.mongodb.client.MongoClient
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.litote.kmongo.KMongo
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

class MongoDBContainerExtension: GenericStaticContainerExtension<MongoDBContainer>()
{
    class MongoResource(val client: MongoClient): ExtensionContext.Store.CloseableResource
    {
        override fun close() {
            client.close()
        }
    }

    companion object {
        const val CLIENT_KEY = "MongoDBContainerExtension::mongodb:client"
    }

    override fun buildContainer(): MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))

    override fun startContainer(container: MongoDBContainer, context: ExtensionContext) {
        System.setProperty("container.mongodb.url", container.getReplicaSetUrl("auth"))

        context.store.getOrComputeIfAbsent(CLIENT_KEY) { container.buildClient() }
    }

    override fun setUpContainer(container: MongoDBContainer, context: ExtensionContext) {

    }

    override fun cleanUpContainer(container: MongoDBContainer, context: ExtensionContext?) {
        val client = context?.client

        client?.apply {
            getDatabase("auth").drop()
        }
    }

    private fun MongoDBContainer.buildClient() = KMongo.createClient(getReplicaSetUrl("auth"))
        .let(::MongoResource)

    private val ExtensionContext.client: MongoClient
        get() = store.get(CLIENT_KEY, MongoResource::class.java)!!.client
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(MongoDBContainerExtension::class)
annotation class WithMongoDBContainer
