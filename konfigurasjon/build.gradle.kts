plugins {
    id("common")
    `java-library`
}

dependencies {
    api(libs.konfig)
    implementation(libs.kotlin.logging)

    api("io.getunleash:unleash-client-java:12.3.2")
}
