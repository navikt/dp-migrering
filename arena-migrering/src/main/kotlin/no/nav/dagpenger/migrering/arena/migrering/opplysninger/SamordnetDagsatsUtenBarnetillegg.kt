package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class SamordnetDagsatsUtenBarnetillegg : OpplysningProdusent<String> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9434-79e8-a64d-1a23cc5d86eb")
    override val opplysningsNavn: String = "Samordnet dagsats uten barnetillegg"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<String> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = "1242",
            gyldigFraOgMed = LocalDate.of(2018, 6, 21),
            uuid = opplysningsId,
        )
}
