package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import tools.jackson.databind.node.NullNode
import java.util.UUID

internal class MigreringStartetMelding(
    private val packet: JsonMessage,
) : KafkaMelding(packet) {
    init {
        packet.interestedIn("sakId", "migreringId", "manglendeOpplysninger")
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    private val sakId: Int = packet["sakId"].asInt()

    override fun hendelse(): Hendelse =
        MigreringStartetHendelse(
            ident = ident,
            sakId = sakId,
            migreringId = migreringId,
            meldingsreferanseId = id,
            type = if (manglendeOpplysninger.isNotEmpty()) "MIGRERING_STARTET_MED_MANGLENDE_OPPLYSNINGER" else "MIGRERING_STARTET",
        )

    internal val manglendeOpplysninger = packet["manglendeOpplysninger"].asArray().toList().map { it["uuid"].asString().toUUID() }

    internal fun publiserManglendeOpplysninger(
        opplysninger: List<Opplysning<*>>,
        publish: (String) -> Unit,
    ) {
        publish(
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
                }.toJson(),
        )
    }
}
