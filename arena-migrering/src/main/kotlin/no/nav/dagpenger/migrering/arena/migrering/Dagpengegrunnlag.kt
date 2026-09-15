package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.util.UUID

class Dagpengegrunnlag : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cbd")

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = "JADA",
            verdi = 1,
            gyldigFraOgMed = LocalDate.now(),
            uuid = opplysningsId,
        )

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
