package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class BrukerenErIkkePavirketAvStreikEllerLockout : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-91df-746a-a8ac-4a6b2b30685f")
    override val opplysningsNavn: String = "Brukeren er ikke påvirket av streik eller lock-out"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
