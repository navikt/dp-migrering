package no.nav.dagpenger.migrering.arena.migrering

import no.nav.dagpenger.inntekt.v1.Inntekt
import java.time.LocalDate

data class ArenaSakMigrering(
    val vilkaarsVurderinger: List<ArenaVilkaarMigrering>,
    val vedtakfakta: List<ArenaVedtakfaktaMigrering>,
    val personopplysninger: List<ArenaPersonMigrering>,
    val personFodselsdatoMigrering: List<ArenaPersonFodselsdatoMigrering>,
    val rettighetstype: List<ArenaRetttighetstypeMigrering>,
    val forbrukteDagerDP: List<ArenaForbrukteDagerMigrering>,
    val forbrukteDagerPERM: List<ArenaForbrukteDagerMigrering>,
    val forbrukteDagerFISK: List<ArenaForbrukteDagerMigrering>,
    val tidsbegrensetVedtak: List<ArenaTidsbegrensetVedtakMigrering>,
    val inntekt: Inntekt?,
    val gjenstaendeTellerMigrering: List<ArenaGjenstaendeTellerMigrering>,
    val opprinneligDPTELMigrering: List<ArenaOpprinneligDPTELMigrering>,
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

    data class ArenaPersonFodselsdatoMigrering(
        val kode: String,
        val verdi: LocalDate?,
        val gyldigFra: LocalDate?,
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

    data class ArenaTidsbegrensetVedtakMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )

    data class ArenaGjenstaendeTellerMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )

    data class ArenaOpprinneligDPTELMigrering(
        val kode: String,
        val verdi: String,
        val gyldigFra: LocalDate,
    )
}
