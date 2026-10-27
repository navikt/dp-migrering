plugins {
    id("ch.acanda.gradle.fabrikt") version "1.45.0"
    id("common")
    idea
    `java-library`
}

tasks {
    compileKotlin {
        dependsOn("fabriktGenerate")
    }
}

tasks.named("runKtlintCheckOverMainSourceSet").configure {
    dependsOn("fabriktGenerate")
}

tasks.named("runKtlintFormatOverMainSourceSet").configure {
    dependsOn("fabriktGenerate")
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/kotlin", "${layout.buildDirectory.get()}/generated/src/main/kotlin"))
        }
    }
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("generated") }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.22")

    testImplementation(libs.bundles.kotest.assertions)
    testImplementation("io.kotest:kotest-runner-junit5:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation("io.kotest:kotest-assertions-json:${libs.versions.kotest.get()}")
    testImplementation("com.approvaltests:approvaltests:31.0.0")
}

fabrikt {
    generate("arena-sak-innsyn-api") {
        apiFile = file("$projectDir/src/main/resources/arena-sak-innsyn-api.yaml")
        basePackage = "no.nav.dagpenger.migrering.arena.api"
        skip = false
        quarkusReflectionConfig = disabled
        typeOverrides {
            datetime = LocalDateTime
        }
        model {
            generate = enabled
            validationLibrary = NoValidation
            extensibleEnums = disabled
            sealedInterfacesForOneOf = enabled
            ignoreUnknownProperties = disabled
            nonNullMapValues = enabled
            serializationLibrary = Jackson
        }
    }

    generate("arena-migrering-api") {
        apiFile = file("$projectDir/src/main/resources/arena-migrering-api.yaml")
        basePackage = "no.nav.dagpenger.arena.migrering.api"
        skip = false
        quarkusReflectionConfig = disabled
        typeOverrides {
            datetime = LocalDateTime
        }
        model {
            generate = enabled
            validationLibrary = NoValidation
            extensibleEnums = disabled
            sealedInterfacesForOneOf = enabled
            ignoreUnknownProperties = disabled
            nonNullMapValues = enabled
            serializationLibrary = Jackson
            suffix = "DTO"
        }
    }
}
