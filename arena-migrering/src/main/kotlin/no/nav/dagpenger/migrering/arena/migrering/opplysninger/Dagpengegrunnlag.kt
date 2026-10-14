package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class Dagpengegrunnlag : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cbd")
    val opplysningsNavn: String = "Dagpengegrunnlag for bruker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.filter { vedtakfakta -> vedtakfakta.kode == "GRUNN" }
        if (vedtakfakta.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vedtakfakta.first().verdi),
                gyldigFraOgMed = vedtakfakta.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne Dagpengegrunnlag for bruker")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
