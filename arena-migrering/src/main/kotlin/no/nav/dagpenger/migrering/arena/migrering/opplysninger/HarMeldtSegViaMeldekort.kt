package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: fikses av Ramp? meldekort dato_innkommet
class HarMeldtSegViaMeldekort : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Resultat
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("01956ab8-126c-7636-803e-a5d87eda2015")
    override val opplysningsNavn: String = "Har meldt seg via meldekort"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = true,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
