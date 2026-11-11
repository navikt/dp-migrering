package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: vedtakfakta VERNP3AV12
class AvtjentVerneplikt : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01948d3c-4bea-7802-9d18-5342a5e2be99")
    override val opplysningsNavn: String = "Avtjent verneplikt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> {
        val vedtakfakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { vedtakfakta -> vedtakfakta.kode == "VERNP3AV12" }

        if (vedtakfakta == null) {
            vedManglendeOpplysning()
        }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(vedtakfakta?.verdi),
            gyldigFraOgMed = vedtakfakta?.gyldigFra,
            uuid = opplysningsId,
        )
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
