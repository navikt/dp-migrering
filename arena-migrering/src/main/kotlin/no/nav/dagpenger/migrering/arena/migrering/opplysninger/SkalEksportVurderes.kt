package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: rettighetkode DEKS
class SkalEksportVurderes : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val opplysningsId: UUID = UUID.fromString("019f608d-f9c5-7ccc-a6f3-c53531d1f00c")
    override val opplysningsNavn: String = "Skal eksport vurderes"
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa

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
            "DEKS" -> {
                true
            }
            else -> {
                false
            }
        }
}
