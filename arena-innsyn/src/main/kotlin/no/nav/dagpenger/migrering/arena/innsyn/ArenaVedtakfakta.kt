package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaVedtakfaktaDTO
import java.time.LocalDate

data class ArenaVedtakfakta(
    val vedtakId: Int,
    val kode: String,
    val navn: String,
    val verdi: String?,
    val registrertDato: LocalDate,
) {
    fun tilKontrakt() =
        ArenaVedtakfaktaDTO(
            kode = kode,
            navn = navn,
            verdi = verdi,
            registrertDato = registrertDato,
        )
}
