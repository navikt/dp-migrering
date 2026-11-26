package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import java.util.UUID

data class OpplysningKontrollResultat(
    val uuid: UUID,
    val navn: String,
    val ok: Boolean,
    val dpSakVerdi: Any?,
    val arenaVerdi: Any?,
)
