package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.arena.migrering.Migrering
import no.nav.dagpenger.migrering.arena.migrering.MigreringMelding

internal class MigreringStartetMelding(
    packet: JsonMessage,
) : MigreringMelding(packet) {
    companion object {
        const val EVENT_NAME = "migrering_startet"

        fun fra(migrering: Migrering): MigreringStartetMelding =
            MigreringStartetMelding(
                JsonMessage
                    .newMessage(
                        EVENT_NAME,
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

    override val hendelse =
        object : Hendelse {
            override fun navn(): String = EVENT_NAME.uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Intern
        }
}
