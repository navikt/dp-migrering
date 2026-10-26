package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class AntallBarnSomGirRettTilBarnetillegg : OpplysningProdusent<Int> {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23c")
    override val opplysningsNavn: String = "Antall barn som gir rett til barnetillegg"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int>? {
        val fakta = kontekst.sakTilMigrering.vedtakfakta.firstOrNull { it.kode == "BARNMSTON" }

        if (fakta == null) {
            logger.info { "Fant ingen fakta for kode BARNMSTON" }
            return null
        }

        return Opplysning(
            navn = opplysningsNavn,
            verdi = fakta.verdi.toInt(),
            gyldigFraOgMed = fakta.gyldigFra,
            uuid = opplysningsId,
        )
    }
}
