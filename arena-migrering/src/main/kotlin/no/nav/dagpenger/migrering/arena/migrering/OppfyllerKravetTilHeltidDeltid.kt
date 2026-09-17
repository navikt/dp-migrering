package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class OppfyllerKravetTilHeltidDeltid : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877bd8")
    val opplysningsNavn: String = "Bruker oppfyller kravet til heltid- og deltidsarbeid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.filter { vilkaar -> vilkaar.kode == "HELDELT" }
        if (vilkaar.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vilkaar.first().verdi),
                gyldigFraOgMed = vilkaar.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne ut av om bruker oppfyller kravet til heltid- og deltidsarbeid")
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
