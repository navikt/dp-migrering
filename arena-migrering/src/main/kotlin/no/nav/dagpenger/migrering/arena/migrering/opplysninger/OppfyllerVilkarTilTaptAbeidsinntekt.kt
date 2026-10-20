package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

class OppfyllerVilkarTilTaptAbeidsinntekt : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a761")
    val opplysningsNavn: String = "Bruker oppfyller kravet til tap av arbeidsinntekt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.filter { vilkaar -> vilkaar.kode == "TAPTINNT" }
        if (vilkaar.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vilkaar.first().verdi),
                gyldigFraOgMed = vilkaar.first().gyldigFra,
                uuid = opplysningsId,
            )
        } else {
            throw IllegalArgumentException("Klarte ikke å finne ut av om bruker oppfyller kravet til tap av arbeidsinntekt")
        }
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
