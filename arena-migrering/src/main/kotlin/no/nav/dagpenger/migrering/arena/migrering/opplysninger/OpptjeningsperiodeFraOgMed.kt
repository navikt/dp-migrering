package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class OpptjeningsperiodeFraOgMed : OpplysningProdusent<LocalDate> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9413-77ce-92ec-d29700f04247")
    override val opplysningsNavn: String = "OpptjeningsperiodeFraOgMed"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<LocalDate> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
