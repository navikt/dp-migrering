package no.nav.dagpenger.migrering.arena.innsyn.person

import no.nav.dagpenger.migrering.arena.api.models.ArenaPersonResponse
import java.time.LocalDate

data class ArenaPerson(
    val personId: Int,
    val fodselsdato: LocalDate?,
    val fodselsnummer: String,
    val etternavn: String,
    val fornavn: String,
) {
    fun tilKontrakt(): ArenaPersonResponse =
        ArenaPersonResponse(
            personId = personId,
            fodselsnummer = fodselsnummer,
            fornavn = fornavn,
            etternavn = etternavn,
        )
}
