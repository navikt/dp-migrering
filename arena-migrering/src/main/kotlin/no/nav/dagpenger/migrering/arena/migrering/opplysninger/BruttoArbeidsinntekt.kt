package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.harSammeInnholdSom
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.tilInntekt
import java.util.UUID

class BruttoArbeidsinntekt : OpplysningProdusent<InntektV1> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

    override val opplysningsId: UUID = UUID.fromString("0194881f-9413-77ce-92ec-d29700f04245")
    override val opplysningsNavn: String = "Brutto arbeidsinntekt"
// Mulig det holder med Inntektsopplysninger (slutter på 244)

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
