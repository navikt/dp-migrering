package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.objectMapper
import java.util.UUID
import no.nav.dagpenger.inntekt.v1.Inntekt as InntektV1

// Arena: Kan den lages fra vedtakfakta i Arena? class inntekt... vedtakfakta DDIDINNTGR INTSISTE / INTP1SLUTT INTNESTS
//   / INTP2SLUTT INTTREDS / INTP3SLUTT
class Inntektsopplysninger : OpplysningProdusent<InntektV1> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val opplysningsId: UUID = UUID.fromString("0194881f-9413-77ce-92ec-d29700f04244")
    override val opplysningsNavn: String = "Inntektsopplysninger"
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    override fun sammenlignVerdier(
        arenaVerdi: Any?,
        dpSakVerdi: Any?,
    ): Boolean {
        val arenaInntekt = arenaVerdi?.tilInntekt()
        val dpSakInntekt = dpSakVerdi?.tilInntekt()
        return dpSakInntekt?.harSammeInnholdSom(arenaInntekt) ?: false
    }

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<InntektV1> {
        val fakta = kontekst.sakTilMigrering.inntekt

        if (fakta == null) {
            vedManglendeOpplysning()
        }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta,
            gyldigFraOgMed = fakta?.hentetTidspunkt?.toLocalDate(),
            uuid = opplysningsId,
        )
    }
}

private fun Any.tilInntekt(): InntektV1 =
    when (this) {
        is InntektV1 -> this
        else -> objectMapper.convertValue(this, InntektV1::class.java)
    }

fun InntektV1.harSammeInnholdSom(other: InntektV1?): Boolean =
    other != null &&
        inntektsId == other.inntektsId &&
        manueltRedigert == other.manueltRedigert &&
        begrunnelseManueltRedigert == other.begrunnelseManueltRedigert &&
        sisteAvsluttendeKalenderMåned == other.sisteAvsluttendeKalenderMåned &&
        hentetTidspunkt == other.hentetTidspunkt &&
        inntektsListe.groupingBy { it }.eachCount() ==
        other.inntektsListe.groupingBy { it }.eachCount()
