package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// QA Ikke OK

class AntallDagerMedSanksjon : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019e82fc-bdd6-77d8-a2bc-4bd7f1d46b25")
    override val opplysningsNavn: String = "Antall dager med sanksjon"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val fakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "SANKLENGDE" }

        if (fakta == null) {
            vedManglendeOpplysning()
        }
        return Opplysning(
            navn = opplysningsNavn,
            verdi = verdi(fakta?.verdi),
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
        )
    }

    private fun verdi(verdi: String?): Int? = verdi?.toInt()?.times(5)
}
