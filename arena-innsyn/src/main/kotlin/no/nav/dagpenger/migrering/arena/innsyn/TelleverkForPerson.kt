package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.TelleverkForPersonResponse

data class TelleverkForPerson(
    val dagpengePeriodeTeller: Int,
    val maxPeriodePermittertTellerFisk: Int?,
    val maxPeriodePermittertTeller: Int?,
) {
    fun tilKontrakt(): TelleverkForPersonResponse =
        TelleverkForPersonResponse(
            dagpengePeriodeTeller = dagpengePeriodeTeller,
            maxPeriodePermittertTellerFisk = maxPeriodePermittertTellerFisk,
            maxPeriodePermittertTeller = maxPeriodePermittertTeller,
        )
}
