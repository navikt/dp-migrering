plugins {
    id("common")
    `java-library`
}
dependencies {
    implementation(project(path = ":openapi"))
    implementation(project(path = ":uuid-v7"))
    implementation(project(path = ":konfigurasjon"))
    implementation(libs.kotlin.logging)
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    implementation(libs.kotlinquery)
    implementation(libs.rapids.and.rivers)
    api("com.github.navikt:dp-inntekt-kontrakter:2_202609181789745424.d9cfdc")

    testImplementation(libs.bundles.kotest.assertions)
    testImplementation("io.kotest:kotest-runner-junit5:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-json:${libs.versions.kotest.get()}")
    testImplementation(libs.rapids.and.rivers.test)
    testImplementation(libs.mockk)
    testImplementation("com.approvaltests:approvaltests:31.0.0")
}
