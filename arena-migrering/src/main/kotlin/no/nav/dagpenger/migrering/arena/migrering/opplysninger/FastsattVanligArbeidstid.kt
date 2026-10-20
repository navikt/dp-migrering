package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class FastsattVanligArbeidstid : OpplysningProdusent<Double> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("019ae986-43bb-7529-af23-9b3d309501eb")

    // Her legges uuid inn for "Sum av fastsatt vanlig arbeidstid"
    // Det er også varianterene "Fastsatt vanlig arbeidstid etter ordinær eller verneplikt" og "Fastsatt vanlig arbeidstid for verneplikt".
    val opplysningsNavn: String = "Dagsats uten barnetillegg samordnet for bruker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Double> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.filter { vedtakfakta -> vedtakfakta.kode == "FASTARBTID" }
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

    private fun verdi(verdi: String?): Double? = verdi?.toDouble()
}
