package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk EGENA
class GjenstaendeEgenandel : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val opplysningsId: UUID = UUID.fromString("01997b70-a12c-7622-bff8-82a20687e640")
    override val opplysningsNavn: String = "Gjenstående egenandel"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 0,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate().minusDays(7),
            uuid = opplysningsId,
        )
}
