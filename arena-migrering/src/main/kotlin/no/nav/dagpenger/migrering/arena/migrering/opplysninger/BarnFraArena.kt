package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.verdier.BarnListe
import java.util.UUID

// Arena: registrerte barn saksopplysning/attributt BARN FNR/FDATO/FNAVN/STONA
class BarnFraArena : OpplysningProdusent<BarnListe> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

// Levere en liste med barn inkl. "all informasjon".
    override val opplysningsId: UUID = UUID.fromString("0194881f-9428-74d5-b160-f63a4c61a23b")
    override val opplysningsNavn: String = "Barn fra Arena"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<BarnListe> =
        Opplysning(
            navn = opplysningsNavn,
            verdi =
                BarnListe(
                    barn = emptyList(),
                ),
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
