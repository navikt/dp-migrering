package no.nav.dagpenger.migrering.arena.migrering

class MigreringsService(
    private val opplysningHandler: List<OpplysningHandler<*>>,
) {
    fun startMigrering(vedtakId: String): List<Opplysning<out Any?>> = opplysningHandler.map { it.håndter() }
}
