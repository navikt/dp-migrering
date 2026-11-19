package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import java.util.UUID

data class OpplysningVerifisertResultat(
    val uuid: UUID,
    val navn: String,
    val verifisert: Boolean,
    val dpSakVerdi: Any?,
    val arenaVerdi: Any?,
)
