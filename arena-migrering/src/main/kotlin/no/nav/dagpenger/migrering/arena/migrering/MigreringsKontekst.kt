package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDateTime
import java.util.UUID

data class MigreringsKontekst(
    val vedtakId: Int,
    val sakId: Int,
    val initiertAv: String,
    val migreringsId: UUID = UUID.randomUUID(),
    val opprettetTidspunkt: LocalDateTime = LocalDateTime.now(),
)
