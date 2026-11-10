package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: SANKLENGDE?
class AntallUkerMedTidsbegrensetBortfall : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019e8300-20f7-7cba-b8dc-7bebfd86dcf3")
    override val opplysningsNavn: String = "Antall uker med tidsbegrenset bortfall"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
