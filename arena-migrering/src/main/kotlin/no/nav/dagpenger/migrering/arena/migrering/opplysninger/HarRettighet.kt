package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class HarRettighet : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")

    // Feil UUID . Finner ikke denne noe sted direkte.
    val opplysningsNavn: String = "Bruker Har rettighet"

    // Denne finner jeg ikke direkte i https://github.com/navikt/dp-behandling/blob/main/docs/opplysninger.approved.md
    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val personopplysninger =
            kontekst.sakTilMigrering.personopplysninger.filter { personopplysninger -> personopplysninger.kode == "RETTIGHETSGRUPPEKODE" }
        if (personopplysninger.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(personopplysninger.first().verdi),
                gyldigFraOgMed = personopplysninger.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne om bruker har rettighet")
    }

    private fun verdi(verdi: String?): Boolean? =
        when (verdi) {
            "DAGP" -> {
                true
            }

            else -> {
                false
            }
        }
}
