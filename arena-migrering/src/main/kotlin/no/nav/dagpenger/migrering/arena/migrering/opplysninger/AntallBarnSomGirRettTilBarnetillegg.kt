package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class AntallBarnSomGirRettTilBarnetillegg : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23c")
    override val opplysningsNavn: String = "Antall barn som gir rett til barnetillegg"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 0,
            gyldigFraOgMed = LocalDate.now(),
            uuid = opplysningsId,
        )
}
