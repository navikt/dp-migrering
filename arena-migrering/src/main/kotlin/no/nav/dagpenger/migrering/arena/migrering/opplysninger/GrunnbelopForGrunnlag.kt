package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class GrunnbelopForGrunnlag : OpplysningProdusent<Double> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa
    override val opplysningsId: UUID = UUID.fromString("0194881f-940f-7af9-9387-052e028b29ee")
    override val opplysningsNavn: String = "Grunnbeløp for grunnlag"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> {
        val grunnbelop = kontekst.sakTilMigrering.grunnbelop.firstOrNull { grunnbelop -> grunnbelop.kode == "GRUNNBELOP" }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = grunnbelop?.verdi?.toDouble(),
            gyldigFraOgMed = grunnbelop?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
