package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class Samordnet : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a250")
    val opplysningsNavn: String = "Er samordning utført"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vedtakfakta.filter { vilkaar -> vilkaar.kode == "SAM" }
        if (vilkaar.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vilkaar.first().verdi),
                gyldigFraOgMed = vilkaar.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne om samordning var utført")
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "J" -> {
                true
            }

            "N" -> {
                false
            }

            else -> {
                null
            }
        }
}
