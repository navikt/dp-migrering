package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class Egenandel : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-943f-78d9-b874-00a4944c54ef")
    override val opplysningsNavn: String = "Egenandel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysning = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "EGENANDDP" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning?.verdi?.toInt(),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
