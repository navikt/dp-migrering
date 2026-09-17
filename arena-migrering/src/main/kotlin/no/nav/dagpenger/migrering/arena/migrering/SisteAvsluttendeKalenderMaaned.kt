package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class SisteAvsluttendeKalenderMaaned : OpplysningProdusent<String> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9414-7823-8d29-0e25b7feb7d0")
    val opplysningsNavn: String = "SisteAvsluttendeKalendrmåned for bruker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<String> {
        val vilkaar = kontekst.sakTilMigrering.vedtakfakta.filter { vilkaar -> vilkaar.kode == "INTP1SLUTT" }
        if (vilkaar.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = vilkaar.first().verdi,
                gyldigFraOgMed = vilkaar.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne SisteAvsluttendeKalendrMåned for bruker")
    }

//    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
