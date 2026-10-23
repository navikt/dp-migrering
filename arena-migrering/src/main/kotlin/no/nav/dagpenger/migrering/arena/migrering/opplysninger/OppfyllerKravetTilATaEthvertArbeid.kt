package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class OppfyllerKravetTilATaEthvertArbeid : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877bdf")
    override val opplysningsNavn: String = "Oppfyller kravet til å ta ethvert arbeid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
