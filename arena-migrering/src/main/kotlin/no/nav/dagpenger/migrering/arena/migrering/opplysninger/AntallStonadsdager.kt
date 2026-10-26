package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class AntallStonadsdager : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-943d-77a7-969c-147999f15457")
    override val opplysningsNavn: String = "Antall stønadsdager"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int>? {
        val fakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "PER" }
                ?: return vedManglendeOpplysning()

        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta.verdi.toInt() * 5, // TODO() Dette må sjekkes
            gyldigFraOgMed = fakta.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
