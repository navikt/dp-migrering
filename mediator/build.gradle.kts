plugins {
    id("common")
    application
    kotlin("plugin.serialization") version "2.4.20"
    alias(libs.plugins.shadow.jar)
}

repositories {
    mavenLocal()
}

dependencies {

    implementation(project(path = ":konfigurasjon"))
    implementation(project(path = ":openapi"))
    implementation(project(path = ":arena-innsyn"))

    implementation(libs.bundles.jackson)
    implementation("no.nav.dagpenger:stpeter-plugin:2026.09.17-12.38.b4f6d88558ca")

    implementation("tools.jackson.module:jackson-module-blackbird:${libs.versions.jackson.get()}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.11.0")
    implementation("com.oracle.database.jdbc:ojdbc11:23.26.3.0.0")
    implementation(libs.hikari)
    implementation(libs.kotlinquery)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)
    implementation(libs.flyway)
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.31.1")
    implementation("io.opentelemetry:opentelemetry-api:1.66.0")
    implementation("io.prometheus:prometheus-metrics-core:1.9.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.17.1")
    implementation("org.slf4j:slf4j-api:2.0.19")
    implementation("ch.qos.logback:logback-classic:1.6.3")

    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation("io.ktor:ktor-server-core-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-cio:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-swagger:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-content-negotiation:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-metrics-micrometer:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-serialization-jackson3:${libs.versions.ktor.get()}")

    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-json:${libs.versions.kotest.get()}")

    testImplementation(libs.mockk)
    testImplementation(libs.mock.oauth2.server)
    testImplementation("io.ktor:ktor-server-test-host-jvm:${libs.versions.ktor.get()}")
    testImplementation("io.ktor:ktor-client-content-negotiation:${libs.versions.ktor.get()}")
    testImplementation("com.approvaltests:approvaltests:31.0.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.5.0")
    testImplementation("io.kotest:kotest-runner-junit5:${libs.versions.kotest.get()}")
    testImplementation("com.h2database:h2:2.5.250")
    testImplementation("no.nav.dagpenger:stpeter-plugin-test:2026.09.17-12.38.b4f6d88558ca")
}

application {
    mainClass.set("no.nav.dagpenger.migrering.AppKt")
}
