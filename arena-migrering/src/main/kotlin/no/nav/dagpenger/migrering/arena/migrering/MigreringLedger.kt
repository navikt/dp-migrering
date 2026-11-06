package no.nav.dagpenger.migrering.arena.migrering

import java.util.UUID

data class MigreringLedger(
    val migreringId: UUID,
    val ident: String,
    val sakId: Int,
    val initiertAv: String,
    val hendelse: String,
    val meldingId: UUID,
    val data: String,
)
