package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

// Arena: Settes til true i db-behandling Antar at gyldig fra blir arvet fra skal vurderes-opplysningen Sender ingenting
//   ?
class PermitteringenFraFiskeindustrienErMidlertidigDriftsinnskrenkningEllerDriftsstans : BoolskOpplysningProdusent {
    override val rolle = OpplysningProdusent.Rolle.Fakta
    override val aktiv: Boolean = true
    override val status: OpplysningProdusent.Status = OpplysningProdusent.Status.Dummy

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override val opplysningsId: UUID = UUID.fromString("019522b8-494f-7012-898c-d202e3b90061")
    override val opplysningsNavn: String = "Permitteringen fra fiskeindustrien er midlertidig driftsinnskrenkning eller driftsstans"

    override suspend fun produser(kontekst: MigreringsKontekst): Opplysning<Boolean> =
        Opplysning(
            navn = opplysningsNavn,
            verdi = false,
            gyldigFraOgMed = kontekst.opprettetTidspunkt.toLocalDate(),
            uuid = opplysningsId,
        )
}
