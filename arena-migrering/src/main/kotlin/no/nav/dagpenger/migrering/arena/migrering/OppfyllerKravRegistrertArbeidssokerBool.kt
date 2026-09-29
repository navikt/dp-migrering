package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class OppfyllerKravRegistrertArbeidssokerBool : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877be1")
    val opplysningsNavn: String = "Oppfyller kravet til å være registrert som arbeidssøker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val personopplysninger =
            kontekst.sakTilMigrering.personopplysninger.filter { personopplysninger -> personopplysninger.kode == "FORMIDLINGSGRUPPEKODE" }
        if (personopplysninger.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(personopplysninger.first().verdi),
                gyldigFraOgMed = personopplysninger.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne om bruker oppfyller kravet til å være registrert som arbeidssøker")
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "ARBS" -> {
                true
            }

            else -> {
                false
            }
        }
}
