package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class OppfyllerKravetTilFramsattSoknad : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("019c092a-7759-700a-9f7d-f4415b91e8db")
    override val opplysningsNavn: String = "Oppfyller kravet til framsatt søknad"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
