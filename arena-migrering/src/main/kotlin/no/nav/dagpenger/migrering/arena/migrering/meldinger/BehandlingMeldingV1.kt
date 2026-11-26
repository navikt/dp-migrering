package no.nav.dagpenger.migrering.arena.migrering.meldinger

import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import java.util.UUID

internal interface BehandlingMeldingV1 :
    KafkaMeldingV1,
    MedMigrering {
    override val migreringId: UUID

    override fun lagreMelding(
        repository: MigreringRepository,
        opprinnelse: KafkaMeldingV1.KafkaMeldingOpprinnelse,
    ) {
        repository.lagreMigreringHendelse(
            migreringId = migreringId,
            meldingId = id,
            hendelse = navn,
            ident = ident,
            jsonData = toJson(),
            opprinnelse = opprinnelse.name,
            tidspunkt = opprettet,
        )
    }
}
