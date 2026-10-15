plugins {
    id("common")
    `java-library`
}
dependencies {
    implementation(libs.kotlin.logging)
    implementation("io.prometheus:prometheus-metrics-core:1.9.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.17.1")
    implementation(libs.kotlinquery)

    testImplementation(libs.bundles.kotest.assertions)
    testImplementation("io.kotest:kotest-runner-junit5:${libs.versions.kotest.get()}")
}
