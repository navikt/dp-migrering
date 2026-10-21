package no.nav.dagpenger.migrering.arena.migrering

import java.time.LocalDate

data class ArenaSakMigrering(
    val vilkaarsVurderinger: List<ArenaVilkaarMigrering>,
    val vedtakfakta: List<ArenaVedtakfaktaMigrering>,
    val personopplysninger: List<ArenaPersonMigrering>,
    val rettighetstype: List<ArenaRetttighetstypeMigrering>,
    val forbrukteDagerDP: List<ArenaForbrukteDagerMigrering>,
    val forbrukteDagerPERM: List<ArenaForbrukteDagerMigrering>,
    val forbrukteDagerFISK: List<ArenaForbrukteDagerMigrering>,
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

    data class ArenaRetttighetstypeMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )

    data class ArenaForbrukteDagerMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )
}
