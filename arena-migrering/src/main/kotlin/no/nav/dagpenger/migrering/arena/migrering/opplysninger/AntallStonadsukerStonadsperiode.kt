package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// QA OK

class AntallStonadsukerStonadsperiode : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val opplysningsType: Class<Int> = Int::class.java

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-943d-77a7-969c-147999f15459")
    override val opplysningsNavn: String = "Antall stønadsuker (stønadsperiode)"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        val fakta =
            kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "PER" }

        if (fakta == null) {
            vedManglendeOpplysning()
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta?.verdi?.toInt(),
            gyldigFraOgMed = fakta?.gyldigFra,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
    }
}
