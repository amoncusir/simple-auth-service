val type_safe: String by project
val config4k: String by project
val ktor_version: String by project
val kotlin_version: String by project
val koin_version: String by project
val logback_version: String by project
val junit_version: String by project
val json_kotlin_test: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
}

group = "info.digitalpoet.auth"
version = "0.0.1"

application {
    mainClass.set("info.digitalpoet.auth.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    google()
}

dependencies {

    implementation("com.typesafe:config:$type_safe")
    implementation("io.github.config4k:config4k:$config4k")

    // Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")

    // Koin
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-common")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-annotations-common")
    }

    // logback
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Kotlin
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")

    //JUnit
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")

    // Json Test
    testImplementation("io.kjson:kjson-test:$json_kotlin_test") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-common")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-annotations-common")
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = false
        events("passed", "skipped", "failed")
    }
}
