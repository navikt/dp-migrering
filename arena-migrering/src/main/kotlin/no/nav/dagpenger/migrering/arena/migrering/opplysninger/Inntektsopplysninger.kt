package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Kan den lages fra vedtakfakta i Arena? class inntekt... vedtakfakta DDIDINNTGR INTSISTE / INTP1SLUTT INTNESTS
//   / INTP2SLUTT INTTREDS / INTP3SLUTT
class Inntektsopplysninger : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Int> = Int::class.java
    override val opplysningsId: UUID = UUID.fromString("0194881f-9413-77ce-92ec-d29700f04244")
    override val opplysningsNavn: String = "Inntektsopplysninger"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
