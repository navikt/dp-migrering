package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDate
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.MigreringMelding
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import tools.jackson.databind.JsonNode
import java.util.UUID

internal class MigreringManglendeOpplysningerMelding(
    private val packet: JsonMessage,
) : MigreringMelding(packet) {
    companion object {
        const val EVENT_NAME = "migrering_manglende_opplysninger"
    }

    init {
        packet.interestedIn("manglendeOpplysninger", "opplysninger")
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = packet["@event_name"].asString().uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Ekstern
        }

//    internal val initiertAv: String = packet["initiertAv"].asString()
//
//    internal val sakId: Int = packet["sakId"].asInt()
//
//    internal val migreringTidspunkt: LocalDate = packet["migreringTidspunkt"].asLocalDate()

    internal fun manglendeOpplysninger() =
        if (!packet["manglendeOpplysninger"].isMissingOrNull()) {
            packet["manglendeOpplysninger"].asArray().toList().map {
                Opplysning(
                    navn = it["navn"].asString(),
                    verdi = null,
                    gyldigFraOgMed = null,
                    uuid = it["uuid"].asString().toUUID(),
                )
            }
        } else {
            emptyList()
        }

    fun opplysninger() =
        if (!packet["opplysninger"].isMissingOrNull()) {
            packet["opplysninger"].asArray().toList().map {
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
