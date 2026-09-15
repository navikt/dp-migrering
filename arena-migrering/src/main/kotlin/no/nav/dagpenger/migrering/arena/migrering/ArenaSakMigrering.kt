package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDate

data class ArenaSakMigrering(
    val vilkaarsVurderinger: List<ArenaVilkaarMigrering>,
    val vedtakfakta: List<ArenaVedtakfaktaMigrering>

) {
    data class ArenaVedtakfaktaMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )

    data class ArenaVilkaarMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )
}
