package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.TelleverkForPersonDTO

data class TelleverkForPerson(
    val ordineerAAPKvote: Int,
    val utvidetAAPKvote: Int?,
) {
    fun tilKontrakt(): TelleverkForPersonDTO =
        TelleverkForPersonDTO(
            ordineerAAPKvote = ordineerAAPKvote,
            utvidetAAPKvote = utvidetAAPKvote,
        )
}
