package no.nav.dagpenger.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.KafkaRapid
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.serialization.jackson3.JacksonConverter
import io.ktor.server.application.install
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import no.dagpenger.ArenaStatistikkRepository
import no.dagpenger.OppdaterMenneskerMetrikk
import no.nav.dagpenger.migrering.api.apiConfig
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.api.statusPagesConfig
import no.nav.dagpenger.migrering.arena.migrering.ArenaRepository
import no.nav.dagpenger.migrering.arena.migrering.BehandlingOpprettetMottak
import no.nav.dagpenger.migrering.arena.migrering.ForslagTilBehandlingsresultatMottak
import no.nav.dagpenger.migrering.arena.migrering.MigreringMediator
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepositoryPostgres
import no.nav.dagpenger.migrering.arena.migrering.StartMigreringMottak
import no.nav.dagpenger.migrering.db.OracleDataSourceBuilder
import no.nav.dagpenger.migrering.db.PostgresDataSourceBuilder
import no.nav.dagpenger.migrering.konfigurasjon.Configuration
import no.nav.helse.rapids_rivers.RapidApplication

internal class ApplicationBuilder(
    config: Map<String, String>,
) : RapidsConnection.StatusListener {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    private val dataSource = OracleDataSourceBuilder().dataSource
    private val postgresDataSourceBuilder = PostgresDataSourceBuilder()

    private val rapidsConnection: RapidsConnection =
        RapidApplication.create(
            env = config,
            builder = {
                withKtorModule {
                    install(ContentNegotiation) {
                        register(ContentType.Application.Json, JacksonConverter(objectMapper))
                    }
                    install(StatusPages) {
                        statusPagesConfig()
                    }
                }
            },
        ) { server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>, rapid: KafkaRapid ->
            val migreringMediator =
                MigreringMediator(
                    arenaRepository = ArenaRepository(dataSource),
                    migreringRepository = MigreringRepositoryPostgres(postgresDataSourceBuilder.dbsession),
                    rapidsConnection = rapid,
                )
            server.application.apiConfig(
                authFactory = AuthFactory(Configuration.properties),
                dataSource = dataSource,
                migreringMediator = migreringMediator,
            )
            StartMigreringMottak(
                rapidsConnection = rapid,
                migreringMediator = migreringMediator,
            )
            BehandlingOpprettetMottak(
                rapidsConnection = rapid,
                migreringMediator = migreringMediator,
            )
            ForslagTilBehandlingsresultatMottak(
                rapidsConnection = rapid,
                migreringMediator = migreringMediator,
            )
        }

    init {
        rapidsConnection.register(this)
    }

    fun start() = rapidsConnection.start()

    fun stop() = rapidsConnection.stop()

    override fun onStartup(rapidsConnection: RapidsConnection) {
        logger.info { "Starter opp dp-migrering" }

        postgresDataSourceBuilder.runMigration()

        OppdaterMenneskerMetrikk.start(ArenaStatistikkRepository(dataSource))
    }
}
