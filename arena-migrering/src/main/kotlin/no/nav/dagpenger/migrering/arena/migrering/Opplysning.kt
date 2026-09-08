package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDate
import java.util.UUID

data class Opplysning<T>(
    val navn: String,
    val verdi: T?,
    val gyldigFraOgMed: LocalDate,
    val uuid: UUID,
)
