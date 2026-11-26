package no.nav.dagpenger.migrering.arena.migrering.meldinger

import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import java.util.UUID

internal interface MedMigrering {
    val migreringId: UUID

    fun lagreMelding(
        repository: MigreringRepository,
        opprinnelse: KafkaMeldingV1.KafkaMeldingOpprinnelse,
    )
}
