package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vedtakfakta PER
class AntallStonadsuker : HeltallsOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert

    override val opplysningsId: UUID = UUID.fromString("0194881f-943d-77a7-969c-147999f15456")
    override val opplysningsNavn: String = "Antall stønadsuker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val fakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "PER" }

        if (fakta == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta?.verdi?.toInt(),
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
