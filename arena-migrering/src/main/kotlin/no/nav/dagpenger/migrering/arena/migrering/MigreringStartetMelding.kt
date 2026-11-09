package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import tools.jackson.databind.node.NullNode
import java.util.UUID

internal class MigreringStartetMelding(
    private val packet: JsonMessage,
    opprinnelse: Hendelse.Opprinnelse,
) : KafkaMelding(packet) {
    companion object {
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
                        ),
                    ),
                Hendelse.Opprinnelse.Intern,
            )
    }

    init {
        packet.interestedIn("sakId", "migreringId", "manglendeOpplysninger")
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    val sakId: Int = packet["sakId"].asInt()

    override val hendelse =
        object : Hendelse {
            override fun navn(): String =
                if (manglendeOpplysninger.isNotEmpty()) "MIGRERING_STARTET_MED_MANGLENDE_OPPLYSNINGER" else "MIGRERING_STARTET"

            override fun opprinnelse(): Hendelse.Opprinnelse = opprinnelse
        }

    internal val manglendeOpplysninger =
        if (!packet["manglendeOpplysninger"].isMissingOrNull()) {
            packet["manglendeOpplysninger"].asArray().toList().map {
                it["uuid"].asString().toUUID()
            }
        } else {
            emptyList()
        }

    internal fun svar(opplysninger: List<Opplysning<*>>): MigreringStartetMelding =
        MigreringStartetMelding(
            packet
                .also {
                    it["manglendeOpplysninger"] = NullNode.instance
                    it["opplysninger"] =
                        opplysninger.map { opplysning ->
                            mapOf(
                                "navn" to opplysning.navn,
                                "verdi" to opplysning.verdi,
                                "gyldigFraOgMed" to opplysning.gyldigFraOgMed?.toString(),
                                "uuid" to opplysning.uuid.toString(),
                            )
                        }
                },
            Hendelse.Opprinnelse.Intern,
        )
}
