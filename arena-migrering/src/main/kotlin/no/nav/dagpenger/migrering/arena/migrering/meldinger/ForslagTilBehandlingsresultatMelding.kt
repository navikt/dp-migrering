package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import no.nav.dagpenger.migrering.arena.migrering.KafkaMelding

internal class ForslagTilBehandlingsresultatMelding(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    override val migreringId = packet["behandletHendelse"]["id"].asString().toUUID()
    override val hendelse: Hendelse =
        object : Hendelse {
            override fun navn(): String = navn.uppercase()

            override fun opprinnelse(): Hendelse.Opprinnelse = Hendelse.Opprinnelse.Ekstern
        }
    val behandlingId = packet["behandlingId"].asString().toUUID()
}
