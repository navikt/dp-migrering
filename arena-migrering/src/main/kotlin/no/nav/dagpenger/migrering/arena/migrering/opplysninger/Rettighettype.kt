package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class Rettighettype : OpplysningProdusent<String> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    val opplysningsNavn: String = "Finne rettighetkode"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<String> {
        val rettighetstype =
            kontekst.sakTilMigrering.rettighetstype.filter { rettighetstype -> rettighetstype.kode == "RETTIGHETKODE" }
        if (rettighetstype.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = rettighetstype.first().verdi,
                gyldigFraOgMed = rettighetstype.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne rettighetkode")
    }
}
