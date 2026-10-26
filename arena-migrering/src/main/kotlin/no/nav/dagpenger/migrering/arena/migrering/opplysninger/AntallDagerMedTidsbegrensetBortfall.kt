package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.time.LocalDate
import java.util.UUID

class AntallDagerMedTidsbegrensetBortfall : OpplysningProdusent<Int> {
    override val opplysningsId: UUID = UUID.fromString("019df7d6-5156-7785-a6fd-54f5af1244be")
    override val opplysningsNavn: String = "Antall dager med tidsbegrenset bortfall"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> {
        // TODO()
        return Opplysning(
            navn = opplysningsNavn,
            verdi = 0,
            gyldigFraOgMed = LocalDate.of(2018, 6, 21),
            uuid = opplysningsId,
        )
    }
}
