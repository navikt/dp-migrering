package no.nav.dagpenger.migrering.arena.innsyn

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
import no.dagpenger.stpeter.plugin.StPeterPlugin
import no.nav.dagpenger.migrering.Ident.Companion.tilPersonIdentfikator
import no.nav.dagpenger.migrering.api.UnprocessableContentException
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.authenticationConfig
import no.nav.dagpenger.migrering.api.token
import no.nav.dagpenger.migrering.arena.api.models.IdentForesporsel
import no.nav.dagpenger.migrering.arena.api.models.PersonIdResponse
import no.nav.dagpenger.migrering.db.OracleDataSourceBuilder
import javax.sql.DataSource

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
                        call.respond(
                            status = HttpStatusCode.OK,
                            message =
                                PersonIdResponse(
                                    id = arenaInnsynResponseService.hentPersonId(ident.identifikator()),
                                ),
                        )
                    }
                }

                get("/person/{personId}") {
                    val personId = call.parameters["personId"]?.toInt() ?: throw BadRequestException("PersonId mangler")

                    val arenaPerson = arenaInnsynResponseService.hentPerson(personId)

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
                        arenaInnsynResponseService.hentArenaSakerForPerson(personId = personId)

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

                    val sak = arenaInnsynResponseService.hentSak(sakId)

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

                    val sak = arenaInnsynResponseService.hentSak(saksnummer)

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
