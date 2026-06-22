package no.nav.dagpenger.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import no.nav.dagpenger.migrering.api.apiConfig
import no.nav.dagpenger.migrering.api.auth.AuthFactory
import no.nav.dagpenger.migrering.konfigurasjon.Configuration

internal class ApplicationBuilder(
    config: Map<String, String>,
) {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    private val server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> =
        ktorApplication(
            preStopHook = {},
            aliveCheck = { true },
            readyCheck = { true },
            cioConfiguration = {
            },
            configuration = {
                apiConfig(AuthFactory(Configuration.properties))
            },
        )

    fun start() {
        server.start(wait = true)
    }
}
