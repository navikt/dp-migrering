package no.nav.dagpenger.migrering.arena.migrering.meldinger

import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import java.time.LocalDate
import java.util.UUID

internal interface MigreringMeldingV1 :
    KafkaMeldingV1,
    MedMigrering {
    override val migreringId: UUID
    val sakId: Int
    val initiertAv: String
    val migreringTidspunkt: LocalDate
    val simulering: Boolean

    override fun lagreMelding(
        repository: MigreringRepository,
        opprinnelse: KafkaMeldingV1.KafkaMeldingOpprinnelse,
    ) {
        repository.lagreMigreringHendelse(
            migreringId = migreringId,
            meldingId = id,
            hendelse = navn.uppercase(),
            ident = ident,
            jsonData = toJson(),
            opprinnelse = opprinnelse.name,
            tidspunkt = opprettet,
        )
    }
}
