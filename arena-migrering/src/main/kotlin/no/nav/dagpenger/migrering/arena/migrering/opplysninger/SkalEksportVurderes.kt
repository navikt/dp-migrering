package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class SkalEksportVurderes : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("019f608d-f9c5-7ccc-a6f3-c53531d1f00c")
    override val opplysningsNavn: String = "Skal eksport vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, false, LocalDate.of(2018, 6, 21), opplysningsId)
}
