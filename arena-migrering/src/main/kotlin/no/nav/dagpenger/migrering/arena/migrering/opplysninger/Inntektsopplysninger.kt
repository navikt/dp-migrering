package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.InntektV1
import java.util.UUID

// Arena: Kan den lages fra vedtakfakta i Arena? class inntekt... vedtakfakta DDIDINNTGR INTSISTE / INTP1SLUTT INTNESTS
//   / INTP2SLUTT INTTREDS / INTP3SLUTT
class Inntektsopplysninger : InntektsOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val opplysningsId: UUID = UUID.fromString("0194881f-9413-77ce-92ec-d29700f04244")
    override val opplysningsNavn: String = "Inntektsopplysninger"
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

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
