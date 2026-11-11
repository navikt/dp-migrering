package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDateTime
import java.util.UUID

data class MigreringHendelse(
    val id: Long,
    val ident: String,
    val migreringId: UUID,
    val meldingId: UUID,
    val sakId: Int,
    val initiertAv: String,
    val hendelse: String,
    val data: String,
    val opprinnelse: String,
    val tidspunkt: LocalDateTime,
)
