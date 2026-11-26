package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class Rettighetstype : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa
    override val opplysningsId: UUID = UUID.fromString("0194881f-9444-7a73-a458-0af81c034d8b")
    override val opplysningsNavn: String = "Rettighetstype"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning = kontekst.sakTilMigrering.rettighetstype.firstOrNull { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(opplysning?.verdi),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            in setOf("DAGO", "LONN", "FISK", "PERM") -> true
            else -> false
        }
}
