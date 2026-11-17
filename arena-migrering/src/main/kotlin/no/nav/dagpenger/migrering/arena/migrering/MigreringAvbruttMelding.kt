package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import java.util.UUID

internal class MigreringAvbruttMelding(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    init {
        packet.interestedIn("migreringId", "årsak")
    }

    companion object {
        const val EVENT_NAME = "migrering_avbrutt"

        fun ny(
            migreringId: UUID,
            sakId: Int,
            initiertAv: String,
            ident: String,
            årsak: Map<String, Any> = emptyMap(),
        ): MigreringAvbruttMelding {
            val packet =
                JsonMessage.newMessage(
                    mapOf(
                        "@event_name" to EVENT_NAME,
                        "migreringId" to migreringId.toString(),
                        "ident" to ident,
                        "sakId" to sakId,
                        "initiertAv" to initiertAv,
                        "årsak" to årsak,
                    ),
                )
            return MigreringAvbruttMelding(packet)
        }
    }

    override val migreringId: UUID = packet["migreringId"].asString().toUUID()
    val årsak = packet["årsak"]
    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = EVENT_NAME.uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Intern
        }
}
