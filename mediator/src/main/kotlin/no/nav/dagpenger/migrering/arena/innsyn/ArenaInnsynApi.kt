package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.dagpenger.stpeter.plugin.StPeterPlugin
import no.dagpenger.stpeter.plugin.TilgangAvvistException
import no.nav.dagpenger.migrering.Ident.Companion.tilPersonIdentfikator
import no.nav.dagpenger.migrering.api.UnprocessableContentException
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.authenticationConfig
import no.nav.dagpenger.migrering.api.token
import no.nav.dagpenger.migrering.arena.api.models.IdentForesporsel
import no.nav.dagpenger.migrering.arena.api.models.PersonIdResponse
import no.nav.dagpenger.migrering.db.OracleDataSourceBuilder
import java.net.URI
import javax.sql.DataSource

/**
 * Slår sammen "ressurs finnes ikke" og "ingen tilgang til ressurs" til samme respons.
 *
 * Uten dette kan en autentisert saksbehandler skille mellom en person/sak som ikke finnes (404)
 * og en som finnes men de mangler tilgang til (403 fra stPeter), og dermed kartlegge hvilke
 * personId/sakId som faktisk finnes i Arena — se threat model T1 (informasjonslekkasje/enumerering).
 * Vi kaller derfor DB-oppslaget før tilgangssjekken (fødselsnummeret trengs for å spørre stPeter),
 * men skjuler NotFoundException bak samme TilgangAvvistException-form som en reell avvisning gir.
 */

private inline fun <T> ApplicationCall.hentEllerAvvisVedIkkeFunnet(hent: () -> T): T =
    try {
        hent()
    } catch (_: NotFoundException) {
        throw TilgangAvvistException(
            title = "Ingen tilgang",
            status = HttpStatusCode.Forbidden,
            type = URI("urn:error:forbidden"),
            detail = "Ingen tilgang til ressursen",
            instance = URI(request.uri),
        )
    }

/**
 * TODO: Oppslagslogg (audit) for innsyn i Arena-data — se threat model T8 (repudiation).
 * Skal den være her eller i dp-stpeter?
 */

internal fun Application.arenaInnsynApi(
    authFactory: AuthFactory,
    dataSource: Lazy<DataSource> = OracleDataSourceBuilder().dataSource,
    stPeter: StPeterPlugin = StPeterPlugin(),
) {
    val arenaInnsynResponseService =
        ArenaInnsynResponseService(
            sakRepository = ArenaSakRepository(dataSource),
            vedtakRepository = ArenaVedtakRepository(dataSource),
            vedtakfaktaRepository = ArenaVedtakFaktaRepository(dataSource),
            vilkårsvurderingRepository = ArenaVilkårsvurderingRepository(dataSource),
            kvoteBrukRepository = ArenaKvoteBrukRepository(dataSource),
            telleverkRepository = ArenaTelleverkRepository(dataSource),
            sakPersonRepository = ArenaSakPersonRepository(dataSource),
            personRepository = ArenaPersonRepository(dataSource),
        )

    authenticationConfig(authFactory)

    routing {
        route("/arena/innsyn") {
            swaggerUI(path = "openapi", swaggerFile = "arena-sak-innsyn-api.yaml", {
            })

            get { call.respond(HttpStatusCode.OK) }

            authenticate("azureAd") {
                post("/person") {
                    val identForespørsel = call.receive<IdentForesporsel>()
                    val ident = identForespørsel.ident.tilPersonIdentfikator()

                    stPeter.vedTilgangTilPerson(ident.identifikator(), call.token()) {
                        val personId = arenaInnsynResponseService.hentPersonId(ident.identifikator())
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = PersonIdResponse(id = personId),
                        )
                    }
                }

                get("/person/{personId}") {
                    val personId = call.parameters["personId"]?.toInt() ?: throw BadRequestException("PersonId mangler")

                    val arenaPerson =
                        call.hentEllerAvvisVedIkkeFunnet { arenaInnsynResponseService.hentPerson(personId) }

                    stPeter.vedTilgangTilPerson(arenaPerson.fodselsnummer, call.token()) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = arenaPerson,
                        )
                    }
                }
                get("/sak/person/{personId}") {
                    val personId = call.parameters["personId"]?.toInt() ?: throw BadRequestException("PersonId mangler")
                    val arenaSakerForPerson =
                        call.hentEllerAvvisVedIkkeFunnet {
                            arenaInnsynResponseService.hentArenaSakerForPerson(personId = personId)
                        }

                    stPeter.vedTilgangTilPerson(arenaSakerForPerson.ident, call.token()) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = arenaSakerForPerson.saker.map { it.tilKontrakt() },
                        )
                    }
                }

                get("/sak/{sakId}/detaljert") {
                    val sakIdParam = call.parameters["sakId"] ?: throw BadRequestException("SakId mangler")
                    val sakId =
                        SakId.fromString(sakIdParam)
                            ?: throw UnprocessableContentException("SakId må være et gyldig heltall")

                    val sak = call.hentEllerAvvisVedIkkeFunnet { arenaInnsynResponseService.hentSak(sakId) }

                    stPeter.vedTilgangTilPerson(sak.person.fodselsnummer, call.token()) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = sak,
                        )
                    }
                }

                get("/sak/{aar}/{lopenummer}/detaljert") {
                    val aarParam = call.parameters["aar"] ?: throw BadRequestException("År mangler")
                    val lopenummerParam =
                        call.parameters["lopenummer"] ?: throw BadRequestException("Løpenummer mangler")
                    val saksnummer =
                        Saksnummer.from(
                            aar = aarParam,
                            lopenummer = lopenummerParam,
                        )
                            ?: throw UnprocessableContentException("Aar og lopenummer mangler eller er ikke gyldige heltall")

                    val sak = call.hentEllerAvvisVedIkkeFunnet { arenaInnsynResponseService.hentSak(saksnummer) }

                    stPeter.vedTilgangTilPerson(sak.person.fodselsnummer, call.token()) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = sak,
                        )
                    }
                }
            }
        }
    }
}
