package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Denne opplysningen settes til true for alle som migreres, da alle som migreres er reelle arbeidssøkere.
// Opplysningen finnes ikke direkte i Arena, men utledes fra andre verdier.
class ReellArbeidssoker : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Verifisert
    override val opplysningsId: UUID = UUID.fromString("0194881f-9442-707b-a6ee-e96c06877be2")
    override val opplysningsNavn: String = "Reell arbeidssøker"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = true,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
