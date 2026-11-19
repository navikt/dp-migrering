plugins {
    id("common")
    `java-library`
}

dependencies {
    api(libs.konfig)
    api(libs.bundles.jackson)
    implementation("tools.jackson.module:jackson-module-blackbird:${libs.versions.jackson.get()}")
    implementation(libs.kotlin.logging)

    api("io.getunleash:unleash-client-java:12.3.2")
}
