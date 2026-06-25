package no.nav.dagpenger.migrering.arena.innsyn

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.authenticationConfig
import no.nav.dagpenger.migrering.db.OracleDataSourceBuilder
import javax.sql.DataSource

internal fun Application.arenaInnsynApi(
    authFactory: AuthFactory,
    dataSource: Lazy<DataSource> = OracleDataSourceBuilder().dataSource,
) {
    val arenaInnsynResponseService =
        ArenaInnsynResponseService(
            sakRepository = ArenaSakRepository(dataSource),
            vedtakRepository = ArenaVedtakRepository(dataSource),
            vedtakfaktaRepository = ArenaVedtakFaktaRepository(dataSource),
            vilkårsvurderingRepository = ArenaVilkårsvurderingRepository(dataSource),
            kvoteBrukRepository = ArenaKvoteBrukRepository(dataSource),
            telleverkRepository = ArenaTelleverkRepository(dataSource),
        )

    authenticationConfig(authFactory)

    routing {
        route("/arena/innsyn") {
            swaggerUI(path = "openapi", swaggerFile = "arena-sak-innsyn-api.yaml")

            get { call.respond(HttpStatusCode.OK) }

            authenticate("azureAd") {
                get("/sak/{sakId}/detaljert") {
                    val sakIdParam = call.parameters["sakId"] ?: throw BadRequestException("SakId mangler")
                    val sakId =
                        SakId.fromString(sakIdParam) ?: throw BadRequestException("SakId må være et gyldig heltall")
                    val sak = arenaInnsynResponseService.hentSak(sakId)
                    call.respond(status = HttpStatusCode.OK, message = sak)
                }

                get("/sak/{aar}/{lopenummer}/detaljert") {
                    val aarParam = call.parameters["aar"] ?: throw BadRequestException("År mangler")
                    val lopenummerParam =
                        call.parameters["lopenummer"] ?: throw BadRequestException("Løpenummer mangler")
                    val saksnummer =
                        Saksnummer.from(
                            aar = aarParam,
                            lopenummer = lopenummerParam,
                        ) ?: throw BadRequestException("Aar og lopenummer mangler eller er ikke gyldige heltall")
                    val sak = arenaInnsynResponseService.hentSak(saksnummer)
                    call.respond(status = HttpStatusCode.OK, message = sak)
                }
            }
        }
    }
}
