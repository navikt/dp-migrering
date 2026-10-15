package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class IkkePavirketAvStrekLockout : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-91df-746a-a8ac-4a6b2b30685f")
    val opplysningsNavn: String = "Brukeren er ikke påvirket av streik eller lock-out"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vilkaar = kontekst.sakTilMigrering.vilkaarsVurderinger.filter { vilkaar -> vilkaar.kode == "RHFRIOSL" }
        if (vilkaar.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(vilkaar.first().verdi),
                gyldigFraOgMed = vilkaar.first().gyldigFra,
                uuid = opplysningsId,
            )
        } else if (vilkaar.size > 1) {
            throw IllegalArgumentException("Klarte ikke å finne ut av om bruker oppfyller kravet til mobilitet")
        } else {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = null,
                gyldigFraOgMed = LocalDate.now(),
                uuid = opplysningsId,
            )
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
