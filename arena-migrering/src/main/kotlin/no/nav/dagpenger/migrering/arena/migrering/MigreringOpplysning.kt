package no.nav.dagpenger.migrering.arena.migrering

import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import java.time.LocalDateTime

data class MigreringOpplysning(
    val opplysning: Opplysning<*>,
    val migrert: Boolean,
    val migrertTidspunkt: LocalDateTime?,
)
