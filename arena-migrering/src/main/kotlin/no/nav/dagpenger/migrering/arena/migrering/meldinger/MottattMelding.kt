package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDateTime
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import java.time.LocalDateTime
import java.util.UUID

internal class MottattMelding<T : KafkaMeldingV1>(
    val innhold: T,
    private val packet: JsonMessage,
) {
    fun packet(): JsonMessage = packet

    val systemParticipatingServices: List<SystemParticipatingService>
        get() =
            if (!packet["system_participating_services"].isMissingOrNull()) {
                packet["system_participating_services"].asArray().toList().map {
                    SystemParticipatingService(
                        id = it["id"].asString().toUUID(),
                        time = it["time"].asLocalDateTime(),
                        service = it["service"].asString(),
                        instance = it["instance"].asString(),
                        image = it["image"].asString(),
                    )
                }
            } else {
                emptyList()
            }
}

data class SystemParticipatingService(
    val id: UUID,
    val time: LocalDateTime,
    val service: String,
    val instance: String,
    val image: String,
)
