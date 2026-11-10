package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.Date
import java.util.UUID

// Arena: Sjekk
class DatoBrukerRegistrerteSegIVertsland : OpplysningProdusent<Date> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019f60cc-dea0-7028-878c-2845f58a6133")
    override val opplysningsNavn: String = "Dato bruker registrerte seg i vertsland"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Date> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
