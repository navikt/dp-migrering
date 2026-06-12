package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaSakPersonDTO

data class ArenaSakPerson(
    val personId: Int,
    val fodselsnummer: String,
    val fornavn: String,
    val etternavn: String,
) {
    fun tilKontrakt(): ArenaSakPersonDTO =
        ArenaSakPersonDTO(
            personId = personId,
            fodselsnummer = fodselsnummer,
            fornavn = fornavn,
            etternavn = etternavn,
        )
}
