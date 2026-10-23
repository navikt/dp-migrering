package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class SkalKravetTilGjenopptakVurderes : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("019d0068-8037-7d91-bfff-de88f9e87fd3")
    override val opplysningsNavn: String = "Skal kravet til gjenopptak vurderes"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, false, LocalDate.of(2018, 6, 21), opplysningsId)
}
