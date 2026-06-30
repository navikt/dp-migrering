package no.nav.dagpenger.migrering.arena.innsyn

import java.time.LocalDate

data class ArenaPerson(
    val personId: Int,
    val fodselsdato: LocalDate?,
    val fodselsnr: String?,
    val etternavn: String,
    val fornavn: String,
)
