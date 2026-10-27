package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class BruktBeregningsregel : OpplysningProdusent<String> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9410-7481-b263-4606fdd10cba")
    override val opplysningsNavn: String = "Brukt beregningsregel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<String> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
