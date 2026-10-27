package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class HarGittAlleNodvendigeOpplysninger : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("01a01e21-17ce-70c4-aac1-d25c30ecfac9")
    override val opplysningsNavn: String = "Har gitt alle nødvendige opplysninger?"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
