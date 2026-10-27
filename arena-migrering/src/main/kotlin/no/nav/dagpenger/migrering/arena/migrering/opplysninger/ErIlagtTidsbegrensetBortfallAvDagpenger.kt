package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class ErIlagtTidsbegrensetBortfallAvDagpenger : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("019df9b1-7052-72e9-9232-547f7cb6ba60")
    override val opplysningsNavn: String = "Er ilagt tidsbegrenset bortfall av dagpenger"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
