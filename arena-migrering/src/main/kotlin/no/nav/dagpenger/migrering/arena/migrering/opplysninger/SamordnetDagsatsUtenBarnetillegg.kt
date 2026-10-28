package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class SamordnetDagsatsUtenBarnetillegg : OpplysningProdusent<Double> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9434-79e8-a64d-1a23cc5d86eb")
    override val opplysningsNavn: String = "Samordnet dagsats uten barnetillegg"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> {
        val opplysning = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "DAGS" }

        if (opplysning == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = opplysning?.verdi?.toDouble(),
            gyldigFraOgMed = opplysning?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
