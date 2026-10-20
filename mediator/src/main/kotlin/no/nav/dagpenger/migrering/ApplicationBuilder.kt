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
import no.nav.dagpenger.migrering.arena.migrering.MigreringsOrchestrator
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Dagpengegrunnlag
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Dagsats
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.InntektPeriode1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.InntektPeriode2
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.InntektPeriode3
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.MedlemAvFolketrygden
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravRegistrertArbeidssoker
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilAlder
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilHeltidDeltid
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppfyllerKravetTilMobilitet
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OppholdINorge
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Samordnet
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.SisteAvsluttendeKalenderManed
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.UkessatsEtterSamordning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.UkessatsForSamordning
import no.nav.dagpenger.migrering.db.OracleDataSourceBuilder
import no.nav.dagpenger.migrering.konfigurasjon.Configuration
import no.nav.helse.rapids_rivers.RapidApplication

internal class ApplicationBuilder(
    config: Map<String, String>,
) : RapidsConnection.StatusListener {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

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
            server.application.apiConfig(
                authFactory = AuthFactory(Configuration.properties),
                migreringsOrchestrator =
                    MigreringsOrchestrator(
                        rapidsConnection = rapid,
                        produsenter =
                            listOf(
                                MedlemAvFolketrygden(),
                                OppholdINorge(),
                                Dagpengegrunnlag(),
                                SisteAvsluttendeKalenderManed(),
                                OppfyllerKravetTilHeltidDeltid(),
                                OppfyllerKravetTilMobilitet(),
                                UkessatsEtterSamordning(),
                                UkessatsForSamordning(),
                                Samordnet(),
                                InntektPeriode1(),
                                InntektPeriode2(),
                                InntektPeriode3(),
                                OppfyllerKravetTilAlder(),
                                Dagsats(),
                                OppfyllerKravRegistrertArbeidssoker(),
                            ),
                        repository = ArenaRepository(OracleDataSourceBuilder().dataSource),
                    ),
            )
        }

    init {
        rapidsConnection.register(this)
    }

    fun start() = rapidsConnection.start()

    fun stop() = rapidsConnection.stop()

    override fun onStartup(rapidsConnection: RapidsConnection) {
        logger.info { "Starter opp dp-migrering" }

        OppdaterMenneskerMetrikk.start(ArenaStatistikkRepository(OracleDataSourceBuilder().dataSource))
    }
}
