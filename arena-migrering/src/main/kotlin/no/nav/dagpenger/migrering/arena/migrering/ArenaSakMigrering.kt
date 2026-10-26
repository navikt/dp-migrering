package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDate

data class ArenaSakMigrering(
    val vilkaarsVurderinger: List<ArenaVilkaarMigrering>,
    val vedtakfakta: List<ArenaVedtakfaktaMigrering>,
    val personopplysninger: List<ArenaPersonMigrering>,
    val tidsbegrensetVedtak: List<ArenaTidsbegrensetVedtakMigrering>,
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

    data class ArenaPersonMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )

    // TODO(): Må sees nærmere på
    data class ArenaTidsbegrensetVedtakMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )
}
