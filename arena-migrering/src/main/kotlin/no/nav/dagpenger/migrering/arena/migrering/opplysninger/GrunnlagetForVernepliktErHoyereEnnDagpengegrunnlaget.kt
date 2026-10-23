package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class GrunnlagetForVernepliktErHoyereEnnDagpengegrunnlaget : OpplysningProdusent<Boolean> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9421-766c-9dc6-41fe6c9a1e05")
    override val opplysningsNavn: String = "Grunnlaget for verneplikt er høyere enn dagpengegrunnlaget"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(opplysningsNavn, false, LocalDate.of(2018, 6, 21), opplysningsId)
}
