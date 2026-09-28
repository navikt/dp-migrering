package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class Dagsats : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-942f-7bde-ab16-68ffd19e9a33")
    val opplysningsNavn: String = "Dagsats uten barnetillegg samordnet for bruker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.filter { vedtakfakta -> vedtakfakta.kode == "DAGS" }
        if (vedtakfakta.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vedtakfakta.first().verdi),
                gyldigFraOgMed = vedtakfakta.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne Dagsats uten barnetillegg samordnet")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
