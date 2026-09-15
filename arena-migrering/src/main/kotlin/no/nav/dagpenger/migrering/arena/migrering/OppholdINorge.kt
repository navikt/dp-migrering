package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class OppholdINorge(
    override val arenaRepository: ArenaRepository,
) : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9443-72b4-8b30-5f6cdb24d54e")

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val rows = arenaRepository.hentVilkaarvurdering(kontekst.vedtakId)
        // ********************************
        // Gjør noe
        // ********************************
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
