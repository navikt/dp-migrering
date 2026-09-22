package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class InntektPeriode2 : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cad")
    val opplysningsNavn: String = "Utbetalt inntekt periode 2"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.filter { vedtakfakta -> vedtakfakta.kode == "INTNESTS" }
        if (vedtakfakta.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vedtakfakta.first().verdi),
                gyldigFraOgMed = vedtakfakta.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne Utbetalt inntekt periode 2")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
