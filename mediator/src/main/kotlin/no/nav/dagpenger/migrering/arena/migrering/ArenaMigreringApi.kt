package no.nav.dagpenger.migrering.arena.migrering

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.dagpenger.migrering.api.FeatureNotEnabledException
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.authenticationConfig
import no.nav.dagpenger.migrering.api.oidcToken
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.unleash

internal fun Application.arenaMigreringApi(
    authFactory: AuthFactory,
    orchestrator: MigreringsOrchestrator,
) {
    authenticationConfig(authFactory)

    routing {
        route("/arena/migrering") {
            swaggerUI(path = "openapi", swaggerFile = "arena-migrering-api.yaml", {
            })

            get { call.respond(HttpStatusCode.OK) }

            authenticate("azureAd") {
                post("/sak/{sakId}") {
                    val sakId = call.parameters["sakId"]?.toInt() ?: throw BadRequestException("SakId mangler")

                    if (unleash.isEnabled(Feature.START_MIGRERING.navn)) {
                        val message =
                            orchestrator.initierMigrering(sakId = sakId, initiertAv = call.oidcToken().navIdent())
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = message,
                        )
                    } else {
                        throw FeatureNotEnabledException(
                            message = "Feature for å starte migrering er deaktivert",
                        )
                    }
                }
            }
        }
    }
}
