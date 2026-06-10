package no.nav.dagpenger.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

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
                install(CallId) {
                    retrieveFromHeader(HttpHeaders.XCorrelationId)
                    generate {
                        java.util.UUID
                            .randomUUID()
                            .toString()
                    }
                }
                install(CallLogging) {
                    logger = LoggerFactory.getLogger("CallLogging")
                    disableDefaultColors()
                    level = Level.INFO
                    callIdMdc(HttpHeaders.XCorrelationId)
                }
                arenaInnsynApi()
            },
        )

    fun start() {
        server.start(wait = true)
    }
}

internal fun Application.arenaInnsynApi() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK)
        }
    }
}
