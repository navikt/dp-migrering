package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDate
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import tools.jackson.databind.JsonNode
import java.util.UUID

internal class MigreringSimuleringResultatMelding(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    init {
        packet.interestedIn(
            "migreringId",
            "ident",
            "simuleringResultat",
        )
    }

    companion object {
        const val EVENT_NAME = "migrering_simulering_resultat"
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = EVENT_NAME.uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Ekstern
        }

    val simuleringResultat =
        if (!packet["simuleringResultat"].isMissingOrNull()) {
            packet["simuleringResultat"].asArray().toList().map {
                Opplysning(
                    navn = it["navn"].asString(),
                    verdi = verdi(it["verdi"]),
                    gyldigFraOgMed = it["gyldigFraOgMed"].asLocalDate(),
                    uuid = it["uuid"].asString().toUUID(),
                )
            }
        } else {
            emptyList()
        }

    private fun verdi(node: JsonNode): Any? =
        when {
            node.isBoolean -> node.asBoolean()
            node.isInt -> node.asInt()
            node.isLong -> node.asLong()
            node.isDouble -> node.asDouble()
            node.isString -> node.asString()
            node.isObject -> node.asObject()
            else -> null
        }
}
