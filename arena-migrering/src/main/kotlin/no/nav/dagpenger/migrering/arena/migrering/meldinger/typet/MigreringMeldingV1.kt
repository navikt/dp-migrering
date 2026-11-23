package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import java.time.LocalDate
import java.util.UUID

internal interface MigreringMeldingV1 : KafkaMeldingV1 {
    val migreringId: UUID
    val sakId: Int
    val initiertAv: String
    val migreringTidspunkt: LocalDate
    val simulering: Boolean

    fun lagreMelding(
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
