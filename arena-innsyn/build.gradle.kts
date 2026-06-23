plugins {
    id("common")
    `java-library`
}
dependencies {
    implementation(project(path = ":openapi"))
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    implementation(libs.kotlinquery)

    testImplementation(libs.bundles.kotest.assertions)
}
