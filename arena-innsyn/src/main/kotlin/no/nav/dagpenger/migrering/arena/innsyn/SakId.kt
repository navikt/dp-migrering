package no.nav.dagpenger.migrering.arena.innsyn

data class SakId(
    val id: Int,
) {
    companion object {
        fun fromString(id: String): SakId? = id.toIntOrNull()?.let { SakId(it) }
    }
}
