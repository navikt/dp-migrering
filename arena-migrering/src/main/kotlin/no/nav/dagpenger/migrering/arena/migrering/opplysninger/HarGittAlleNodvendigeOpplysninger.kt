package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class HarGittAlleNodvendigeOpplysninger : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("01a01e21-17ce-70c4-aac1-d25c30ecfac9")
    override val opplysningsNavn: String = "Har gitt alle nødvendige opplysninger?"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
