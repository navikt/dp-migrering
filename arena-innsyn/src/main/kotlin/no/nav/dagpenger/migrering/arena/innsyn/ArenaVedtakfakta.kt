package no.nav.dagpenger.migrering.arena.innsyn

import no.nav.dagpenger.migrering.arena.api.models.ArenaVedtakfaktaDTO
import java.time.LocalDateTime

data class ArenaVedtakfakta(
    val vedtakId: Int,
    val kode: String,
    val navn: String,
    val verdi: String?,
    val registrertDato: LocalDateTime,
) {
    fun tilKontrakt() =
        ArenaVedtakfaktaDTO(
            kode = kode,
            navn = navn,
            verdi = verdi,
            registrertDato = registrertDato.toLocalDate(),
        )
}
