package no.nav.dagpenger.migrering.api

import io.ktor.http.HttpStatusCode
import java.net.URI

// TODO: Har vi riktig navn på denne? Sannsynligvis ikke!
sealed class BehandlingException(
    val httpStatus: HttpStatusCode,
    val type: URI,
    val title: String,
    val extensions: Map<String, Any?> = emptyMap(),
    message: String = title,
) : RuntimeException(message)
