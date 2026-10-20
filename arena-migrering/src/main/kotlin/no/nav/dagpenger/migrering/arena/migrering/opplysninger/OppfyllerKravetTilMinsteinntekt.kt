package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst

class OppfyllerKravetTilMinsteinntekt : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9413-77ce-92ec-d29700f0424c")
    val opplysningsNavn: String = "Bruker oppfyller kravet til minsteinntekt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.filter { vilkaar -> vilkaar.kode == "MININT2003" }
        if (vilkaar.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vilkaar.first().verdi),
                gyldigFraOgMed = vilkaar.first().gyldigFra,
                uuid = opplysningsId,
            )
        } else {
            throw IllegalArgumentException("Klarte ikke å finne ut av om bruker oppfyller kravet til minsteinntekt")
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
