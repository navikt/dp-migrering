package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaSakPersonDTO
import java.time.LocalDate

data class ArenaPerson(
    val personId: Int,
    val fodselsdato: LocalDate?,
    val fodselsnummer: String?,
    val etternavn: String,
    val fornavn: String,
) {
    fun tilKontrakt(): ArenaSakPersonDTO =
        ArenaSakPersonDTO(
            personId = personId,
            fodselsnummer = fodselsnummer,
            fornavn = fornavn,
            etternavn = etternavn,
        )
}
