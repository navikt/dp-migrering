package no.nav.dagpenger.migrering.arena.migrering

import java.util.UUID

internal class BehandlingOpprettetHendelse(
    private val ident: String,
    private val migreringId: UUID,
    private val meldingsreferanseId: UUID,
    private val type: String,
) : Hendelse {
    override fun type(): String = type

    override fun ident(): String = ident

    override fun migreringId(): UUID = migreringId

    override fun meldingsreferanseId(): UUID = meldingsreferanseId
}
