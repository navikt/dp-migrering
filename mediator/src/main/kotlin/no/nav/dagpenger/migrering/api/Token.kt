package no.nav.dagpenger.migrering.api

import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException

fun ApplicationCall.token(): String {
    val authHeader = requireNotNull(this.request.headers[HttpHeaders.Authorization]) { "Mangler Authorization-header" }
    val (scheme, token) =
        authHeader.split(" ", limit = 2).takeIf { it.size == 2 }
            ?: throw BadRequestException("Ugyldig Authorization-header")

    if (!scheme.equals("Bearer", ignoreCase = true)) {
        throw BadRequestException("Ugyldig Authorization-header: forventet Bearer-scheme")
    }

    return token
}
