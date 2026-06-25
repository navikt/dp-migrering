package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.TelleverkForPersonDTO

data class TelleverkForPerson(
    val dagpengePeriodeTeller: Int,
    val maxPeriodePermittertTellerFisk: Int?,
    val maxPeriodePermittertTeller: Int?,
) {
    fun tilKontrakt(): TelleverkForPersonDTO =
        TelleverkForPersonDTO(
            dagpengePeriodeTeller = dagpengePeriodeTeller,
            maxPeriodePermittertTellerFisk = maxPeriodePermittertTellerFisk,
            maxPeriodePermittertTeller = maxPeriodePermittertTeller,
        )
}
