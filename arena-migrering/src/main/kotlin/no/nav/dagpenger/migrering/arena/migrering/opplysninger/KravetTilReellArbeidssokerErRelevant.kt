package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class KravetTilReellArbeidssokerErRelevant : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194ff86-a035-7eb0-9c60-21899f7cc0c1")
    override val opplysningsNavn: String = "Kravet til reell arbeidssøker er relevant"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
