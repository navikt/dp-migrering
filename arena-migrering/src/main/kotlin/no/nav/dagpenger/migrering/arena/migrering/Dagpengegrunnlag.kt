package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class Dagpengegrunnlag(
    override val arenaRepository: ArenaRepository,
) : OpplysningProdusent<Int> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cbd")

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val rows = arenaRepository.hentVedtakfakta(kontekst.vedtakId)
        // ********************************
        // Gjør noe
        // ********************************
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()
}
