package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID

internal class BehandlingOpprettetMelding(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    override val migreringId = packet["behandletHendelse"]["id"].asString().toUUID()
    val behandlingId = packet["behandlingId"].asString().toUUID()

    override fun hendelse() =
        BehandlingOpprettetHendelse(
            ident = ident,
            migreringId = migreringId,
            meldingsreferanseId = id,
            type = navn.uppercase(),
        )
}
