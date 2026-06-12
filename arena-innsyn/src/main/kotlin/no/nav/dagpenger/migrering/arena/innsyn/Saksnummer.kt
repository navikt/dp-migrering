package no.nav.dagpenger.migrering.arena.innsyn

data class Saksnummer(
    val lopenummer: Int,
    val aar: Int,
) {
    companion object {
        fun fromString(id: String): Saksnummer? {
            if (!id.contains('-')) {
                return null
            }

            val (aar, lopenr) = id.split('-')
            return Saksnummer(lopenummer = lopenr.toInt(), aar = aar.toInt())
        }
    }
}
