package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class MedlemHarReduserteYtelserFraFolketrygden : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9434-79e8-a64d-1a23cc5d86ea")
    override val opplysningsNavn: String = "Medlem har reduserte ytelser fra folketrygden (Samordning)"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, false, LocalDate.of(2018, 6, 21), opplysningsId)
}
