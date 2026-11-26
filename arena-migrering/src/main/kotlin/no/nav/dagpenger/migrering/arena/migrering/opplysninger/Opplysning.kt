package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ConsistentCopyVisibility
data class Opplysning<T : Any> private constructor(
    val navn: String,
    val verdi: T?,
    val gyldigFraOgMed: LocalDate?,
    val uuid: UUID,
) {
    companion object {
        operator fun invoke(
            navn: String,
            verdi: BigDecimal?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<BigDecimal>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: String?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<String>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: LocalDate?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<LocalDate>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: Boolean?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<Boolean>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: Int?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<Int>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: Double?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<Double>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: BarnListe?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<BarnListe>(navn, verdi, gyldigFraOgMed, uuid)

        operator fun invoke(
            navn: String,
            verdi: InntektV1?,
            gyldigFraOgMed: LocalDate?,
            uuid: UUID,
        ) = Opplysning<InntektV1>(navn, verdi, gyldigFraOgMed, uuid)
    }
}
