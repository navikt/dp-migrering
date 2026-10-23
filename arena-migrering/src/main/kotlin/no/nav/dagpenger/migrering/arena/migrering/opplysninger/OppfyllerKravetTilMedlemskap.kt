package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class OppfyllerKravetTilMedlemskap : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9443-72b4-8b30-5f6cdb24d54d")
    override val opplysningsNavn: String = "Oppfyller kravet til medlemskap"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
