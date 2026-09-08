package no.nav.dagpenger.migrering.arena.migrering

import java.util.UUID

data class Opplysning<T>(
    val navn: String,
    val verdi: T,
    val gyldigFraOgMed: String,
    val uuid: UUID,
)
