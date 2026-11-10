package no.nav.dagpenger.migrering.arena.migrering

import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import java.util.UUID

interface MigreringRepository {
    fun lagreMigrering(message: Migrering)

    fun hentMigrering(migreringId: UUID): Migrering?

    fun lagreOpplysninger(
        migreringId: UUID,
        opplysninger: List<Opplysning<*>>,
    )

    fun markereOpplysningerSomMigrert(
        migreringId: UUID,
        opplysninger: List<Opplysning<*>>,
    )

    fun markereBehandlingOpprettet(
        migreringId: UUID,
        behandlingId: UUID,
    )

    fun hentManglendeOpplysningerForMigrering(
        migreringId: UUID,
        opplysninger: List<UUID>,
    ): List<MigreringOpplysning>

    fun hentOpplysningerForMigrering(migreringId: UUID): List<MigreringOpplysning>

    fun lagreMigreringHendelse(
        migreringId: UUID,
        meldingId: UUID,
        hendelse: String,
        ident: String,
        jsonData: String,
        opprinnelse: String,
    )

    fun migreringHendelser(
        migreringId: UUID,
        lastEventId: Long = 0,
        limit: Int = 10,
    ): List<MigreringHendelse>
}
