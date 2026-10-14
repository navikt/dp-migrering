plugins {
    id("common")
    `java-library`
}

dependencies {
    api(libs.konfig)
    implementation(libs.kotlin.logging)
}
