package no.nav.dagpenger.migrering.arena.migrering

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.dagpenger.stpeter.plugin.StPeterPlugin
import no.dagpenger.stpeter.plugin.objectMapper
import no.nav.dagpenger.arena.migrering.api.models.MigreringHendelseResponse
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringRequest
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringResponse
import no.nav.dagpenger.migrering.api.FeatureNotEnabledException
import no.nav.dagpenger.migrering.api.oidcToken
import no.nav.dagpenger.migrering.api.sse.SseInnslag
import no.nav.dagpenger.migrering.api.sse.SseKilde
import no.nav.dagpenger.migrering.api.sse.opprettSseStream
import no.nav.dagpenger.migrering.api.sse.sseCursorEller
import no.nav.dagpenger.migrering.api.token
import no.nav.dagpenger.migrering.arena.innsyn.hentEllerAvvisVedIkkeFunnet
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.unleash
import java.util.UUID

internal fun Application.arenaMigreringApi(
    migreringMediator: MigreringMediator,
    stPeter: StPeterPlugin = StPeterPlugin(),
) {
    routing {
        route("/arena/migrering") {
            swaggerUI(path = "openapi", swaggerFile = "arena-migrering-api.yaml", {
            })

            get { call.respond(HttpStatusCode.OK) }

            authenticate("azureAd") {
                post("/sak/{sakId}") {
                    val sakId = call.parameters["sakId"]?.toInt() ?: throw BadRequestException("SakId mangler")

                    if (unleash.isEnabled(Feature.START_MIGRERING.navn)) {
                        val startMigreringRequest = call.receive<StartMigreringRequest>()
                        val migrering =
                            migreringMediator.startMigrering(
                                sakId = sakId,
                                initiertAv = call.oidcToken().navIdent(),
                                simulering = startMigreringRequest.simulering,
                                migreringsTidspunkt = startMigreringRequest.migreringsTidspunkt,
                            )
                        call.respond(
                            HttpStatusCode.Created,
                            StartMigreringResponse(
                                sakId = sakId,
                                migreringId = migrering.migreringId,
                                simulering = migrering.simulering,
                                initiertAv = migrering.initiertAv,
                                ident = migrering.ident,
                                migreringsTidspunkt = migrering.migreringTidspunkt,
                                opprettetTidspunkt = migrering.opprettet,
                            ),
                        )
                    } else {
                        throw FeatureNotEnabledException(
                            message = "Feature for å starte migrering er deaktivert",
                        )
                    }
                }
                get("/{migreringId}") {
                    val migreringId =
                        try {
                            UUID.fromString(call.parameters["migreringId"])
                        } catch (_: IllegalArgumentException) {
                            throw BadRequestException("Ugyldig migreringId")
                        }
                    val migrering =
                        withContext(Dispatchers.IO) {
                            call.hentEllerAvvisVedIkkeFunnet {
                                migreringMediator.hentMigrering(migreringId)
                                    ?: throw NotFoundException("Migrering finnes ikke")
                            }
                        }

                    stPeter.vedTilgangTilPerson(
                        ident = migrering.ident,
                        token = call.token(),
                        originRoute = "/arena/migrering/{migreringId}",
                    ) {
                        call.respond(
                            HttpStatusCode.OK,
                            StartMigreringResponse(
                                sakId = migrering.sakId,
                                migreringId = migrering.migreringId,
                                simulering = migrering.simulering,
                                initiertAv = migrering.initiertAv,
                                ident = migrering.ident,
                                migreringsTidspunkt = migrering.migreringTidspunkt,
                                opprettetTidspunkt = migrering.opprettet,
                                status = migrering.status,
                            ),
                        )
                    }
                }
                get("/{migreringId}/oppdateringer") {
                    val migreringId =
                        try {
                            UUID.fromString(call.parameters["migreringId"])
                        } catch (_: IllegalArgumentException) {
                            throw BadRequestException("Ugyldig migreringId")
                        }
                    val cursor = call.sseCursorEller { 0 }
                    val migrering =
                        withContext(Dispatchers.IO) {
                            call.hentEllerAvvisVedIkkeFunnet {
                                migreringMediator.hentMigrering(migreringId)
                                    ?: throw NotFoundException("Migrering finnes ikke")
                            }
                        }
                    val token = call.oidcToken()
                    stPeter.vedTilgangTilPerson(
                        ident = migrering.ident,
                        token = call.token(),
                        originRoute = "/arena/migrering/{migreringId}/oppdateringer",
                    ) {
                        call.opprettSseStream(
                            kilde =
                                SseKilde(
                                    defaultCursor = { cursor },
                                    hentInnslag = { lastEventId ->
                                        migreringMediator
                                            .hentOppdateringer(migreringId, lastEventId, limit = 1)
                                            .map {
                                                SseInnslag(
                                                    id = it.id,
                                                    data =
                                                        objectMapper.writeValueAsString(
                                                            mapOf(
                                                                "id" to it.id,
                                                                "hendelse" to it.hendelse,
                                                                "tidspunkt" to it.tidspunkt,
                                                                "opprinnelse" to it.opprinnelse,
                                                            ),
                                                        ),
                                                )
                                            }
                                    },
                                ),
                            erAutentisert = token::isNotExpired,
                        )
                    }
                }
                get("/hendelse/{hendelseId}") {
                    val hendelseId =
                        try {
                            call.parameters["hendelseId"]?.toLong()
                        } catch (_: NumberFormatException) {
                            throw BadRequestException("Ugyldig hendelseId")
                        }
                            ?: throw BadRequestException("HendelseId mangler")

                    val migreringHendelse =
                        withContext(Dispatchers.IO) {
                            call.hentEllerAvvisVedIkkeFunnet {
                                migreringMediator.hentMigreringHendelse(hendelseId)
                                    ?: throw NotFoundException("Migrering hendelse finnes ikke")
                            }
                        }

                    stPeter.vedTilgangTilPerson(
                        ident = migreringHendelse.ident,
                        token = call.token(),
                        originRoute = "/arena/migrering/hendelse/{hendelseId}",
                    ) {
                        call.respond(
                            HttpStatusCode.OK,
                            MigreringHendelseResponse(
                                id = migreringHendelse.id,
                                meldingId = migreringHendelse.meldingId,
                                sakId = migreringHendelse.sakId,
                                hendelse = migreringHendelse.hendelse,
                                tidspunkt = migreringHendelse.tidspunkt,
                                opprinnelse = migreringHendelse.opprinnelse,
                                data = migreringHendelse.data,
                                initiertAv = migreringHendelse.initiertAv,
                            ),
                        )
                    }
                }

                get("/sak/{sakId}") {
                    val sakId = call.parameters["sakId"]?.toInt() ?: throw BadRequestException("SakId mangler")

                    val migreringer =
                        withContext(Dispatchers.IO) {
                            migreringMediator.hentMigreringerForSak(sakId)
                        }

                    if (migreringer.isEmpty()) {
                        throw NotFoundException("Ingen migreringer funnet for sak $sakId")
                    }

                    stPeter.vedTilgangTilPerson(
                        ident = migreringer.first().ident,
                        token = call.token(),
                        originRoute = "/arena/migrering/sak/{sakId}",
                    ) {
                        call.respond(
                            HttpStatusCode.OK,
                            migreringer.map {
                                StartMigreringResponse(
                                    migreringId = it.migreringId,
                                    sakId = it.sakId,
                                    ident = it.ident,
                                    initiertAv = it.initiertAv,
                                    simulering = it.simulering,
                                    migreringsTidspunkt = it.migreringTidspunkt,
                                    opprettetTidspunkt = it.opprettet,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
