package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class ForbrukteDagerFISK : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    val opplysningsNavn: String = "Forbrukte dager FISK"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val forbrukteDagerFISK =
            kontekst.sakTilMigrering.forbrukteDagerFISK.filter { forbrukteDagerFISK ->
                forbrukteDagerFISK.kode == "ForbrukteDagerFISK"
            }
        if (forbrukteDagerFISK.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(forbrukteDagerFISK.first().verdi),
                gyldigFraOgMed = forbrukteDagerFISK.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne Forbrukte dager FISK")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
