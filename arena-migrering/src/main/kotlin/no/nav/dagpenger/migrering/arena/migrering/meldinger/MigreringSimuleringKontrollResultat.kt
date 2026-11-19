package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage

internal class MigreringSimuleringKontrollResultat(
    private val packet: JsonMessage,
) : MigreringMelding(packet) {
    companion object {
        const val EVENT_NAME = "migrering_simulering_kontroll_resultat"
    }

    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = EVENT_NAME.uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Ekstern
        }
}
