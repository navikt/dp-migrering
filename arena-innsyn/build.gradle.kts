plugins {
    id("common")
    `java-library`
}
dependencies {
    implementation(project(path = ":openapi"))
    api(libs.kotlinquery)

    testImplementation(libs.bundles.kotest.assertions)
}
