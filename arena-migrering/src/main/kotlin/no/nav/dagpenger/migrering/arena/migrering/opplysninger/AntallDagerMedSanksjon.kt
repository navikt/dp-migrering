package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class AntallDagerMedSanksjon : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("019e82fc-bdd6-77d8-a2bc-4bd7f1d46b25")
    override val opplysningsNavn: String = "Antall dager med sanksjon"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(opplysningsNavn, 0, LocalDate.of(2018, 6, 21), opplysningsId)
}
