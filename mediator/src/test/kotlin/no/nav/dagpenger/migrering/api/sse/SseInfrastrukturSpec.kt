package no.nav.dagpenger.migrering.api.sse

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson3.JacksonConverter
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import no.nav.dagpenger.migrering.api.statusPagesConfig
import no.nav.dagpenger.migrering.objectMapper
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

class SseInfrastrukturSpec :
    StringSpec({
        "sender hendelser i rekkefølge og bruker siste sendte ID ved neste henting" {
            val cursors = mutableListOf<Long>()
            testApplication {
                application {
                    install(SSE)
                    routing {
                        get("/events") {
                            call.opprettSseStream(
                                SseKilde(
                                    defaultCursor = { 0 },
                                    hentInnslag = { cursor ->
                                        cursors.add(cursor)
                                        when (cursor) {
                                            0L -> listOf(SseInnslag(2, "startet", """{"status":"startet"}"""))
                                            2L -> listOf(SseInnslag(5, "opprettet", """{"status":"opprettet"}"""))
                                            else -> emptyList()
                                        }
                                    },
                                ),
                                erAutentisert = { cursors.size < 3 },
                            )
                        }
                    }
                }
                val response = client.get("/events")
                response.status shouldBe HttpStatusCode.OK
                response.headers["Content-Type"] shouldBe "text/event-stream"
                response.headers["Cache-Control"] shouldBe "no-store"
                response.headers["X-Accel-Buffering"] shouldBe "no"
                response.bodyAsText().replace("\r\n", "\n") shouldBe
                    "event: startet\ndata: {\"status\":\"startet\"}\nid: 2\n\n" +
                    "event: opprettet\ndata: {\"status\":\"opprettet\"}\nid: 5\n\n" +
                    "event: close\ndata: {\"reason\":\"reauth\"}\n\n"
                cursors shouldBe listOf(0L, 2L, 5L)
            }
        }

        listOf(
            Triple(null, null, 0L),
            Triple(null, "4", 4L),
            Triple("7", "4", 7L),
        ).forEach { (header, since, expected) ->
            "cursor fra header=$header og since=$since gir $expected" {
                testApplication {
                    application {
                        routing {
                            get("/cursor") {
                                call.respond(call.sseCursorEller { 0 }.toString())
                            }
                        }
                    }
                    client
                        .get("/cursor${since?.let { "?since=$it" } ?: ""}") {
                            header?.let { header("Last-Event-ID", it) }
                        }.bodyAsText() shouldBe expected.toString()
                }
            }
        }

        listOf("-1", "abc", "9223372036854775808", "").forEach { cursor ->
            "avviser ugyldig cursor '$cursor' også når since er gyldig" {
                testApplication {
                    application {
                        install(ContentNegotiation) {
                            register(ContentType.Application.Json, JacksonConverter(objectMapper))
                        }
                        install(StatusPages) { statusPagesConfig() }
                        routing {
                            get("/cursor") {
                                call.respond(call.sseCursorEller { 0 }.toString())
                            }
                        }
                    }
                    client
                        .get("/cursor?since=1") {
                            header("Last-Event-ID", cursor)
                        }.status shouldBe HttpStatusCode.BadRequest
                }
            }
        }

        "avviser strøm nummer 101 og frigjør plasser når forespørslene avsluttes" {
            testApplication {
                val startet = Channel<Unit>(Channel.UNLIMITED)
                val avslutt = CompletableDeferred<Unit>()
                application {
                    install(ContentNegotiation) {
                        register(ContentType.Application.Json, JacksonConverter(objectMapper))
                    }
                    routing {
                        get("/slot") {
                            call.medSseStreamSlot {
                                startet.send(Unit)
                                avslutt.await()
                                call.respond(HttpStatusCode.NoContent)
                            }
                        }
                    }
                }
                val strømmer = List(100) { async { client.get("/slot") } }
                try {
                    repeat(100) { startet.receive() }
                    val response = client.get("/slot")
                    response.status shouldBe HttpStatusCode.TooManyRequests
                    response.headers["Retry-After"] shouldBe "2"
                } finally {
                    avslutt.complete(Unit)
                    strømmer.awaitAll()
                }
                client.get("/slot").status shouldBe HttpStatusCode.NoContent
            }
        }

        "heartbeat-klokken nullstilles ved sending uten å forlenge maksimal levetid" {
            val timeSource = TestTimeSource()
            val klokke = SseStreamKlokke.start(timeSource)
            klokke.skalSendeHeartbeat() shouldBe false
            timeSource += 20.seconds
            klokke.skalSendeHeartbeat() shouldBe true
            klokke.registrerSendt()
            klokke.skalSendeHeartbeat() shouldBe false
            timeSource += 15.minutes - 20.seconds
            klokke.harNåddMaksVarighet() shouldBe true
        }
    })
