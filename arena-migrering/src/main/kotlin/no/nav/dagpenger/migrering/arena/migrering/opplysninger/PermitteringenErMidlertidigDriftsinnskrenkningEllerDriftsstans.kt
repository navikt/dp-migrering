package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Settes til true i db-behandling Antar at gyldig fra blir arvet fra skal vurderes-opplysningen Sender ingenting
//   ?
class PermitteringenErMidlertidigDriftsinnskrenkningEllerDriftsstans : OpplysningProdusent<Boolean> {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = false

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("0194d119-90b7-7416-a8b0-9e9cf3587d48")
    override val opplysningsNavn: String = "Permitteringen er midlertidig driftsinnskrenkning eller driftsstans"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = null,
            gyldigFraOgMed = null,
            uuid = opplysningsId,
        )
}
