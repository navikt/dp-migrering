package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import tools.jackson.databind.json.JsonMapper
import java.util.UUID

internal class MigreringStartetMelding(
    private val packet: JsonMessage,
) : KafkaMelding(packet) {
    companion object {
        private val mapper = JsonMapper.builder().build()

        fun fra(migrering: Migrering): MigreringStartetMelding =
            MigreringStartetMelding(
                JsonMessage
                    .newMessage(
                        "migrering_startet",
                        mapOf(
                            "sakId" to migrering.sakId,
                            "migreringTidspunkt" to migrering.migreringTidspunkt.toString(),
                            "simulering" to migrering.simulering,
                            "initiertAv" to migrering.initiertAv,
                            "ident" to migrering.ident,
                            "migreringId" to migrering.migreringId.toString(),
                            "opplysninger" to emptyList<Any>(),
                        ),
                    ),
            )
    }

    init {
        packet.interestedIn("sakId", "migreringId", "initiertAv", "simulering")
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    val sakId: Int = packet["sakId"].asInt()
    val initiertAv: String = packet["initiertAv"].asString()

    override val hendelse =
        object : Hendelse {
            override fun navn(): String = packet["@event_name"].asString().uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Intern
        }

    val simulering = if (!packet["simulering"].isMissingOrNull()) packet["simulering"].asBoolean() else false
}
