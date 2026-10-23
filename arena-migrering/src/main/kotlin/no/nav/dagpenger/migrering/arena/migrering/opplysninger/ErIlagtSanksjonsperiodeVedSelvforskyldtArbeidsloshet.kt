package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class ErIlagtSanksjonsperiodeVedSelvforskyldtArbeidsloshet : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("019e82fc-04e7-7f8d-bd52-6a7041ebb15f")
    override val opplysningsNavn: String = "Er ilagt sanskjonsperiode ved selvforskyldt arbeidsløshet"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, false, LocalDate.of(2018, 6, 21), opplysningsId)
}
