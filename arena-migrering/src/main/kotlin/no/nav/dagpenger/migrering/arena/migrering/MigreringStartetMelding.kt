package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import tools.jackson.databind.json.JsonMapper
import java.util.UUID

internal class MigreringStartetMelding(
    private val packet: JsonMessage,
    opprinnelse: Hendelse.Opprinnelse,
    hendelse: String,
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
                Hendelse.Opprinnelse.Intern,
                "MIGRERING_STARTET",
            )
    }

    init {
        packet.interestedIn("sakId", "migreringId", "manglendeOpplysninger", "opplysninger")
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    val sakId: Int = packet["sakId"].asInt()

    override val hendelse =
        object : Hendelse {
            override fun navn(): String = hendelse

            override fun opprinnelse(): Hendelse.Opprinnelse = opprinnelse
        }

    internal val manglendeOpplysninger =
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
//    internal val opplysninger =
//        if (!packet["opplysninger"].isMissingOrNull()) {
//            packet["opplysninger"].asArray().toList().map { opplysning ->
//                Opplysning(
//                    navn = opplysning["navn"].asString(),
//                    verdi = opplysning["verdi"].asString(),
//                    gyldigFraOgMed = opplysning["gyldigFraOgMed"].asOptionalLocalDate(), // .asString()?.let { LocalDate.parse(it) },
//                    uuid = opplysning["uuid"].asString().toUUID(),
//                )
//            }
//        } else {
//            emptyList()
//        }

    internal fun svar(opplysninger: List<Opplysning<*>>): MigreringStartetMelding =
        MigreringStartetMelding(
            JsonMessage
                .newMessage(
                    mapper
                        .readTree(packet.toJson())
                        .properties()
                        .filterNot { it.key == "manglendeOpplysninger" }
                        .associate { it.key to it.value },
                ).also {
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
            "MIGRERING_STARTET_OPPLYSNINGER",
        )
}
