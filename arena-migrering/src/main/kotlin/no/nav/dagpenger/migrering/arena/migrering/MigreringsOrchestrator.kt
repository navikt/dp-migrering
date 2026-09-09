package no.nav.dagpenger.migrering.arena.migrering

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.util.UUID

class MigreringsOrchestrator(
    private val produsenter: List<OpplysningProdusent<*>>,
) {
    suspend fun initierMigrering(
        vedtakId: Int,
        sakId: Int,
        initiertAv: String,
    ): List<Opplysning<*>> =
        coroutineScope {
            val deferredOpplysninger =
                produsenter.map { produsent ->
                    async {
                        try {
                            produsent.produser(
                                MigreringsKontekst(
                                    vedtakId = vedtakId,
                                    sakId = sakId,
                                    migreringsId = UUID.randomUUID(),
                                    initiertAv = initiertAv,
                                    opprettetTidspunkt = LocalDateTime.now(),
                                ),
                            )
                        } catch (e: Exception) {
                            throw MigreringsException("Feilet i ${produsent.opplysningsId}", e)
                        }
                    }
                }

            deferredOpplysninger.awaitAll()
        }
}
