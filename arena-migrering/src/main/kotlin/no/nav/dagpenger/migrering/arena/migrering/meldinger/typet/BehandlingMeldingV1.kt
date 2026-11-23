package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import java.util.UUID

internal interface BehandlingMeldingV1 : KafkaMeldingV1 {
    val migreringId: UUID

    fun lagreMelding(
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
