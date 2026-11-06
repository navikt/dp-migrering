package no.nav.dagpenger.migrering.arena.migrering

import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import java.util.UUID

interface MigreringRepository {
    fun lagre(message: Migrering)

    fun hentMigrering(migreringId: UUID): Migrering?

    fun lagreOpplysninger(
        migreringId: UUID,
        opplysninger: List<Opplysning<*>>,
    )

    fun markereOpplysningerSomMigrert(
        migreringId: UUID,
        opplysninger: List<Opplysning<*>>,
    )

    fun hentManglendeOpplysningerForMigrering(
        migreringId: UUID,
        opplysninger: List<UUID>,
    ): List<MigreringOpplysning>

    fun hentOpplysningerForMigrering(migreringId: UUID): List<MigreringOpplysning>
}
