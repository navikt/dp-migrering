package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class OppfyllerKravRegistrertArbeidssoker : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877be1")
    override val opplysningsNavn: String = "Oppfyller kravet til å være registrert som arbeidssøker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val personopplysninger =
            kontekst.sakTilMigrering.personopplysninger
                .firstOrNull { personopplysninger -> personopplysninger.kode == "FORMIDLINGSGRUPPEKODE" }

        if (personopplysninger == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(personopplysninger?.verdi),
            gyldigFraOgMed = personopplysninger?.gyldigFra,
            uuid = opplysningsId,
        )
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
