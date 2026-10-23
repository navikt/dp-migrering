package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class AntallStonadsuker : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-943d-77a7-969c-147999f15459")
    override val opplysningsNavn: String = "Antall stønadsuker (stønadsperiode)"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 104,
            gyldigFraOgMed = LocalDate.of(2018, 6, 21),
            uuid = opplysningsId,
        )
}
