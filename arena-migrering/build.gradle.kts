plugins {
    id("common")
    `java-library`
}
dependencies {
    implementation(libs.kotlin.logging)
    implementation(project(path = ":openapi"))
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    implementation(libs.kotlinquery)
    implementation(libs.rapids.and.rivers)

    testImplementation(libs.bundles.kotest.assertions)
    testImplementation("io.kotest:kotest-runner-junit5:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-json:${libs.versions.kotest.get()}")
    testImplementation(libs.rapids.and.rivers.test)
    testImplementation("com.approvaltests:approvaltests:31.0.0")
}
