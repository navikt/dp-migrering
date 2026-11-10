package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Settes til true i db-behandling Antar at gyldig fra blir arvet fra skal vurderes-opplysningen
//   Sender ingenting ?
class ArsakenTilPermitteringenFraFiskeindustrienErGodkjent : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019522b8-0f1b-7536-8691-fd824bca86de")
    override val opplysningsNavn: String = "Årsaken til permitteringen fra fiskeindustrien er godkjent"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
