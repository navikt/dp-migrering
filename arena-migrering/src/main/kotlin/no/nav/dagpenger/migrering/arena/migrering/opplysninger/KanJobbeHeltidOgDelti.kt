package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vilkår HELDELT
class KanJobbeHeltidOgDelti : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

    override val opplysningsId: UUID = UUID.fromString("0194881f-9441-7d1b-a06a-6727543a141e")
    override val opplysningsNavn: String = "Kan jobbe heltid og deltid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.firstOrNull { vilkaar -> vilkaar.kode == "HELDELT" }

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
