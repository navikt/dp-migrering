package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class FastsattArbeidstidPerUkeForTap : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a76a")
    override val opplysningsNavn: String = "Fastsatt arbeidstid per uke for tap"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val opplysning = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "FASTARBTID" }

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
