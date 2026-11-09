package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import java.util.UUID

internal class ForslagTilBehandlingsresultatMelding(
    packet: JsonMessage,
) : KafkaMelding(packet) {
    override val migreringId = packet["behandletHendelse"]["id"].asString().toUUID()

    override fun hendelse() =
        object : Hendelse {
            override fun type(): String = navn.uppercase()

            override fun ident(): String = ident

            override fun migreringId(): UUID = migreringId

            override fun meldingsreferanseId(): UUID = id
        }

    val behandlingId = packet["behandlingId"].asString().toUUID()
}
