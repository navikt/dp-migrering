package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

class KravetTilReellArbeidssokerErRelevant : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false
    override val opplysningsType: Class<Boolean> = Boolean::class.java
    override val opplysningsId: UUID = UUID.fromString("0194ff86-a035-7eb0-9c60-21899f7cc0c1")
    override val opplysningsNavn: String = "Kravet til reell arbeidssøker er relevant"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
            type = opplysningsType.kotlin,
        )
}
