package no.nav.dagpenger.migrering.api.sse

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.sse.SSEServerContent
import io.ktor.server.sse.ServerSSESession
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import no.nav.dagpenger.migrering.arena.api.models.HttpProblem
import java.net.URI
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

internal data class SseInnslag(
    val id: Long,
    val event: String,
    val data: String,
)

internal data class SseKilde(
    val defaultCursor: () -> Long,
    val hentInnslag: suspend (cursor: Long) -> List<SseInnslag>,
)

internal suspend fun ApplicationCall.streamSse(
    initialCursor: Long,
    erAutentisert: () -> Boolean,
    hentInnslag: suspend (cursor: Long) -> List<SseInnslag>,
) {
    val førsteInnslag = withContext(Dispatchers.IO) { hentInnslag(initialCursor) }
    response.headers.append("Cache-Control", "no-store")
    response.headers.append("X-Accel-Buffering", "no")
    respond(
        SSEServerContent(this) {
            var cursor = initialCursor
            var innslag = førsteInnslag
            val klokke = SseStreamKlokke.start()
            while (coroutineContext.isActive) {
                if (!erAutentisert() || klokke.harNåddMaksVarighet()) {
                    sendLukkForReautentisering()
                    break
                }

                val nyCursor = sendNyeInnslag(cursor, innslag)
                if (nyCursor != cursor) {
                    cursor = nyCursor
                    klokke.registrerSendt()
                } else if (klokke.skalSendeHeartbeat()) {
                    sendHeartbeat()
                    klokke.registrerSendt()
                }
                if (innslag.isEmpty()) delay(SSE_POLL_INTERVAL)
                innslag = withContext(Dispatchers.IO) { hentInnslag(cursor) }
            }
        },
    )
}

internal suspend fun ApplicationCall.opprettSseStream(
    kilde: SseKilde,
    erAutentisert: () -> Boolean,
) {
    medSseStreamSlot {
        streamSse(sseCursorEller(kilde.defaultCursor), erAutentisert, kilde.hentInnslag)
    }
}

private suspend fun ServerSSESession.sendNyeInnslag(
    cursor: Long,
    innslag: List<SseInnslag>,
): Long {
    var nyCursor = cursor
    innslag.forEach { innslag ->
        check(innslag.id > nyCursor) { "SSE-hendelser må ha stigende ID" }
        send(
            ServerSentEvent(
                data = innslag.data,
                event = innslag.event,
                id = innslag.id.toString(),
            ),
        )
        nyCursor = innslag.id
    }
    return nyCursor
}

private suspend fun ServerSSESession.sendLukkForReautentisering() {
    send(ServerSentEvent(data = """{"reason":"reauth"}""", event = "close"))
}

private suspend fun ServerSSESession.sendHeartbeat() {
    send(ServerSentEvent(data = "{}", event = "ping"))
}

internal fun ApplicationCall.sseCursorEller(defaultCursor: () -> Long): Long {
    val råCursor = request.headers["Last-Event-ID"] ?: request.queryParameters["since"] ?: return defaultCursor()
    val cursor = råCursor.toLongOrNull() ?: throw BadRequestException("Ugyldig cursor i since/Last-Event-ID")
    if (cursor < 0) throw BadRequestException("Cursor må være 0 eller større")
    return cursor
}

internal suspend fun ApplicationCall.medSseStreamSlot(block: suspend () -> Unit) {
    if (!sseStreamLimiter.tryAcquire()) {
        response.header("Content-Type", ContentType.Application.ProblemJson.toString())
        response.header("Retry-After", "2")
        respond(
            HttpStatusCode.TooManyRequests,
            HttpProblem(
                type = URI("urn:error:too_many_requests"),
                title = "For mange samtidige SSE-strømmer",
                status = HttpStatusCode.TooManyRequests.value,
                instance = URI(request.uri),
            ),
        )
        return
    }
    try {
        block()
    } finally {
        sseStreamLimiter.release()
    }
}

private val SSE_POLL_INTERVAL = 2.seconds
private val SSE_HEARTBEAT_INTERVAL = 20.seconds
private val SSE_MAKS_VARIGHET = 15.minutes
private val sseStreamLimiter = Semaphore(100)

internal class SseStreamKlokke private constructor(
    private val timeSource: TimeSource,
    private var sistSendt: TimeMark,
    private val startet: TimeMark,
) {
    fun harNåddMaksVarighet(): Boolean = startet.elapsedNow() >= SSE_MAKS_VARIGHET

    fun skalSendeHeartbeat(): Boolean = sistSendt.elapsedNow() >= SSE_HEARTBEAT_INTERVAL

    fun registrerSendt() {
        sistSendt = timeSource.markNow()
    }

    companion object {
        fun start(timeSource: TimeSource = TimeSource.Monotonic): SseStreamKlokke {
            val nå = timeSource.markNow()
            return SseStreamKlokke(timeSource = timeSource, sistSendt = nå, startet = nå)
        }
    }
}
