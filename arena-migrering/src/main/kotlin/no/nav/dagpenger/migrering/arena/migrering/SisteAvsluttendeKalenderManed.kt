package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class SisteAvsluttendeKalenderManed : OpplysningProdusent<LocalDate> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9414-7823-8d29-0e25b7feb7d0")
    val opplysningsNavn: String = "SisteAvsluttendeKalendrmåned for bruker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<LocalDate> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.filter { vedtakfakta -> vedtakfakta.kode == "INTP1SLUTT" }
        if (vedtakfakta.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vedtakfakta.first().verdi),
                gyldigFraOgMed = vedtakfakta.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne SisteAvsluttendeKalendrMåned for bruker")
    }

    private fun verdi(verdi: String?): LocalDate? =
        verdi?.let {
            YearMonth.parse(it).atEndOfMonth()
        }
}
