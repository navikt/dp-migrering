package no.nav.dagpenger.migrering.arena.innsyn

data class Saksnummer(
    val lopenummer: Int,
    val aar: Int,
) {
    companion object {
        fun from(
            aar: String,
            lopenummer: String,
        ): Saksnummer? =
            try {
                Saksnummer(lopenummer = lopenummer.toInt(), aar = aar.toInt())
            } catch (_: NumberFormatException) {
                null
            }
    }

    fun formatert(): String = "$aar-$lopenummer"
}
