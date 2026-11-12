package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: kvotebruk DPTEL
class AntallDagerSomErForbrukt : OpplysningProdusent<Int> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01992934-66e4-7606-bdd3-c6c9dd420ffd")
    override val opplysningsNavn: String = "Antall dager som er forbrukt"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Int> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = 123,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate().minusDays(7),
            uuid = opplysningsId,
        )
}
