package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.util.UUID

interface OpplysningProdusent<T : Any> {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    val opplysningsId: UUID
    val opplysningsNavn: String

    suspend fun produser(kontekst: MigreringsKontekst): Opplysning<T>

    fun vedManglendeOpplysning() {
        logger.info { "Fant ikke opplysning: $opplysningsNavn ($opplysningsId)" }
    }
}
