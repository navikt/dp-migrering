package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.Date
import java.util.UUID

// Arena: Dato samme som med perm migreringsdato?
class DatoEksportSkalGjeldeFra : OpplysningProdusent<Date> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019f60cb-737b-70c2-8d9f-779dea0b6e3f")
    override val opplysningsNavn: String = "Dato eksport skal gjelde fra"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Date> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
