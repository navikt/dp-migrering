package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import java.util.UUID

internal class MigreringOpplysningerMelding(
    private val packet: JsonMessage,
) : KafkaMelding(packet) {
    init {
        packet.interestedIn("migreringId", "initiertAv")
    }

    override val migreringId: UUID = packet["migreringId"].asUUID()
    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = packet["@event_name"].asString().uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Ekstern
        }

    internal val initiertAv: String = packet["initiertAv"].asString()

    companion object {
        fun fra(
            migreringId: UUID,
            sakId: Int,
            ident: String,
            initiertAv: String,
            opplysninger: List<Opplysning<*>>,
        ): MigreringOpplysningerMelding {
            val packet =
                JsonMessage.newMessage(
                    "migrering_opplysninger",
                    mapOf(
                        "migreringId" to migreringId.toString(),
                        "ident" to ident,
                        "sakId" to sakId,
                        "initiertAv" to initiertAv,
                        "opplysninger" to
                            opplysninger.map { opplysning ->
                                mapOf(
                                    "navn" to opplysning.navn,
                                    "verdi" to opplysning.verdi,
                                    "gyldigFraOgMed" to opplysning.gyldigFraOgMed?.toString(),
                                    "uuid" to opplysning.uuid.toString(),
                                )
                            },
                    ),
                )
            return MigreringOpplysningerMelding(packet)
        }
    }
}
