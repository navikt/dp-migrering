package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDate
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import java.time.LocalDate
import java.util.UUID

internal abstract class MigreringMelding(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    init {
        packet.interestedIn("sakId", "initiertAv", "migreringTidspunkt", "migreringId", "simulering")
    }

    val sakId: Int = packet["sakId"].asInt()
    val initiertAv: String = packet["initiertAv"].asString()
    val migreringTidspunkt: LocalDate = packet["migreringTidspunkt"].asLocalDate()
    val simulering: Boolean = if (!packet["simulering"].isMissingOrNull()) packet["simulering"].asBoolean() else false
    override val migreringId: UUID = packet["migreringId"].asUUID()
}
