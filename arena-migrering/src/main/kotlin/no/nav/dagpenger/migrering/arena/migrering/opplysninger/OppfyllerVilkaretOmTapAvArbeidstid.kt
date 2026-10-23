package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class OppfyllerVilkaretOmTapAvArbeidstid : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9435-72a8-b1ce-9575cbc2a76e")
    override val opplysningsNavn: String = "Oppfyller vilkåret om tap av arbeidstid"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, true, LocalDate.of(2018, 6, 21), opplysningsId)
}
