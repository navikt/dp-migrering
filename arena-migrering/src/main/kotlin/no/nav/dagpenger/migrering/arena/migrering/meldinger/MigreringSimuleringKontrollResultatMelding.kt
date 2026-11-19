package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OpplysningVerifisertResultat
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.node.ObjectNode
import java.time.LocalDate
import java.util.UUID

internal class MigreringSimuleringKontrollResultatMelding(
    packet: JsonMessage,
) : MigreringMelding(packet) {
    companion object {
        const val EVENT_NAME = "migrering_simulering_kontroll_resultat"

        fun fra(migreringSimuleringKontrollResultat: MigreringSimuleringKontrollResultat): MigreringSimuleringKontrollResultatMelding {
            val jsonMessage =
                MigreringSimuleringKontrollResultatMelding(
                    toJsonMessage(
                        EVENT_NAME,
                        migreringSimuleringKontrollResultat,
                    ),
                )
            return jsonMessage
        }
    }

    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = EVENT_NAME.uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Intern
        }
}

fun toJsonMessage(
    eventName: String,
    dto: Any,
): JsonMessage {
    val node = objectMapper.valueToTree<ObjectNode>(dto)
    node.put("@event_name", eventName)
    val json = objectMapper.writeValueAsString(node)
    return JsonMessage(json, MessageProblems(json))
}

data class MigreringSimuleringKontrollResultat(
    val migreringId: UUID,
    val ident: String,
    val sakId: Int,
    val initiertAv: String,
    val migreringTidspunkt: LocalDate,
    val simulering: Boolean,
    val verifisert: List<OpplysningVerifisertResultat>,
    val ikkeVerifisert: List<OpplysningVerifisertResultat>,
)
