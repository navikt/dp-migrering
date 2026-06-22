package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.authenticationConfig

internal fun Application.arenaInnsynApi(authFactory: AuthFactory) {
    authenticationConfig(authFactory)

    routing {
        route("/arena/innsyn") {
            swaggerUI(path = "openapi", swaggerFile = "arena-sak-innsyn-api.yaml")

            get { call.respond(HttpStatusCode.OK) }

            authenticate("azureAd") {
                get("/sak/{sakId}/detaljert") {
                    val sakId = call.parameters["sakId"] ?: throw IllegalArgumentException("SakId mangler i path")
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
