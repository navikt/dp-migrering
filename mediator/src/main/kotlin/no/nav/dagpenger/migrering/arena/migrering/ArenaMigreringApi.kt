package no.nav.dagpenger.migrering.arena.migrering

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringDTO
import no.nav.dagpenger.migrering.api.FeatureNotEnabledException
import no.nav.dagpenger.migrering.api.oidcToken
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.unleash

internal fun Application.arenaMigreringApi(orchestrator: MigreringMediator) {
    routing {
        route("/arena/migrering") {
            swaggerUI(path = "openapi", swaggerFile = "arena-migrering-api.yaml", {
            })

            get { call.respond(HttpStatusCode.OK) }

            authenticate("azureAd") {
                get("/sak/{sakId}") {
                    val sakId = call.parameters["sakId"]?.toInt() ?: throw BadRequestException("SakId mangler")

                    if (unleash.isEnabled(Feature.START_MIGRERING.navn)) {
                        val message =
                            orchestrator.hentMigreringGrunnlag(sakId = sakId, initiertAv = call.oidcToken().navIdent())
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

                post("/sak/{sakId}") {
                    val sakId = call.parameters["sakId"]?.toInt() ?: throw BadRequestException("SakId mangler")

                    if (unleash.isEnabled(Feature.START_MIGRERING.navn)) {
                        val startMigreringDTO = call.receive<StartMigreringDTO>()
                        orchestrator.startMigrering(startMigreringDTO)
                        call.respond(HttpStatusCode.Accepted)
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
