package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class OppfyllerKravetTilVerneplikt : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("01948d43-e218-76f1-b29b-7e604241d98a")
    override val opplysningsNavn: String = "Oppfyller kravet til verneplikt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val opplysning = kontekst.sakTilMigrering.vilkaarsVurderinger.firstOrNull { it.kode == "IKKEVP" }

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

    fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "N" -> true
            "J" -> false
            else -> null
        }
}
