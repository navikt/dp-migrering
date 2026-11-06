package no.nav.dagpenger.migrering.arena.migrering

import no.nav.dagpenger.uuid.UUIDv7
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Migrering(
    val migreringId: UUID = UUIDv7.ny(),
    val ident: String,
    val sakId: Int,
    val initiertAv: String,
    val opprettet: LocalDateTime = LocalDateTime.now(),
    val migreringsTidspunkt: LocalDate,
    val simulering: Boolean = false,
)
