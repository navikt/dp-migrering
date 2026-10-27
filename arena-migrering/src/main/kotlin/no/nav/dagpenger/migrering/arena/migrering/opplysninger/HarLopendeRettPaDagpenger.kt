package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class HarLopendeRettPaDagpenger : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("01990a09-0eab-7957-b88f-14484a50e194")
    override val opplysningsNavn: String = "Har løpende rett på dagpenger"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
