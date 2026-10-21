package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class ForbrukteDagerPERM : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    val opplysningsNavn: String = "Forbrukte dager PERM"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val forbrukteDagerPERM =
            kontekst.sakTilMigrering.forbrukteDagerPERM.filter { forbrukteDagerPERM ->
                forbrukteDagerPERM.kode == "ForbrukteDagerPERM"
            }
        if (forbrukteDagerPERM.size == 1) {
            return Opplysning(
                navn = opplysningsNavn,
                verdi = verdi(forbrukteDagerPERM.first().verdi),
                gyldigFraOgMed = forbrukteDagerPERM.first().gyldigFra,
                uuid = opplysningsId,
            )
        }
        throw IllegalArgumentException("Klarte ikke å finne Forbrukte dager PERM")
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
