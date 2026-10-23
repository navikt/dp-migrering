package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class Provingsdato : OpplysningProdusent<LocalDate> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-91d1-7df2-ba1d-4533f37fcc76")
    override val opplysningsNavn: String = "Prøvingsdato"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<LocalDate> =
        Opplysning(opplysningsNavn, LocalDate.of(2018, 6, 21), LocalDate.of(2018, 6, 21), opplysningsId)
}
