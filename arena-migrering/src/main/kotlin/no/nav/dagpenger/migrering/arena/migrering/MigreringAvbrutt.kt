package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import java.util.UUID

internal class MigreringAvbrutt(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    init {
        packet.interestedIn("migreringId", "årsak")
    }

    companion object {
        fun ny(
            migreringId: UUID,
            sakId: Int,
            initiertAv: String,
            ident: String,
            årsak: Map<String, Any> = emptyMap(),
        ): MigreringAvbrutt {
            val packet =
                JsonMessage.newMessage(
                    mapOf(
                        "@event_name" to "migrering_avbrutt",
                        "migreringId" to migreringId.toString(),
                        "ident" to ident,
                        "sakId" to sakId,
                        "initiertAv" to initiertAv,
                        "årsak" to årsak,
                    ),
                )
            return MigreringAvbrutt(packet)
        }
    }

    override val migreringId: UUID = packet["migreringId"].asString().toUUID()
    val årsak = packet["årsak"]
    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = "MIGRERING_AVBRUTT"

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Intern
        }
}
