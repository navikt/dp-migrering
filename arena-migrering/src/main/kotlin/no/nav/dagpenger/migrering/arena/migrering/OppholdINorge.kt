package no.nav.dagpenger.migrering.arena.migrering

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import java.util.UUID

class OppholdINorge : OpplysningProdusent<Boolean> {
    companion object {
        val sikkerlogg = KotlinLogging.logger("tjenestekall.DpTilgangProvider")
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9443-72b4-8b30-5f6cdb24d54e")

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = "TULL",
            verdi = false,
            gyldigFraOgMed = LocalDate.now(),
            uuid = opplysningsId,
        )

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
