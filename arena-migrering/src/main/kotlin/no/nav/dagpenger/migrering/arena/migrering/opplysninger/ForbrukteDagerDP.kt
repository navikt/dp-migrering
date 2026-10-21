package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class ForbrukteDagerDP : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    val opplysningsNavn: String = "Forbrukte dager DP"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val forbrukteDagerDP =
            kontekst.sakTilMigrering.forbrukteDagerDP.filter { forbrukteDagerDP ->
                forbrukteDagerDP.kode ==
                    "ForbrukteDagerDP"
            }
        if (forbrukteDagerDP.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(forbrukteDagerDP.first().verdi),
                gyldigFraOgMed = forbrukteDagerDP.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne Forbrukte dager DP")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
