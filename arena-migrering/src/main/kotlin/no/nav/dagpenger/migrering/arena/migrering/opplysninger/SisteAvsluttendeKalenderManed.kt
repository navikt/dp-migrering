package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class SisteAvsluttendeKalenderManed : OpplysningProdusent<LocalDate> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9414-7823-8d29-0e25b7feb7d0")
    override val opplysningsNavn: String = "SisteAvsluttendeKalendrmåned for bruker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<LocalDate> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { vedtakfakta -> vedtakfakta.kode == "INTP1SLUTT" }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(vedtakfakta?.verdi),
            gyldigFraOgMed = vedtakfakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): LocalDate? =
        verdi?.let {
            YearMonth.parse(it).atEndOfMonth()
        }
}
