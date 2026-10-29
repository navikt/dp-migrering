package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import java.util.UUID

interface MigreringRepository {
    fun lagre(message: JsonMessage)

    fun hentMigrering(migreringId: UUID): List<MigreringLedger>
}
