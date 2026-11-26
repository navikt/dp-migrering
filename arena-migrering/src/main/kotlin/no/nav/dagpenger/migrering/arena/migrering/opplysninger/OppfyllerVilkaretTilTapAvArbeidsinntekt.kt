package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkår TAPTINNT
class OppfyllerVilkaretTilTapAvArbeidsinntekt : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Alfa
    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a761")
    override val opplysningsNavn: String = "Oppfyller vilkåret til tap av arbeidsinntekt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.firstOrNull { vilkaar -> vilkaar.kode == "TAPTINNT" }

        if (vilkaar == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(vilkaar?.verdi),
            gyldigFraOgMed = vilkaar?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "J" -> {
                true
            }

            "N" -> {
                false
            }

            else -> {
                null
            }
        }
}
