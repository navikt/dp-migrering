package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkår siste vedtak OATVIST
class BrukerenDeltarIStreikEllerErOmfattetAvlockOut : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

    override val opplysningsId: UUID = UUID.fromString("0194881f-91df-746a-a8ac-4a6b2b30685d")
    override val opplysningsNavn: String = "Brukeren deltar i streik eller er omfattet av lock-out"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val fakta =
            kontekst.sakTilMigrering.vilkaarsVurderinger.firstOrNull { it.kode == "OATVIST" }

        if (fakta == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(fakta?.verdi),
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    // I Arena er J at bruker ikke deltar i streik eller er omfattet av lock-out. N blir defor true her da vi svarer på om brukeren er omfattet,
    private fun verdi(fakta: String?): Boolean? =
        when (fakta) {
            "N" -> true
            "J" -> false
            else -> null
        }
}
