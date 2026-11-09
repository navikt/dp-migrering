package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger.aktiveProdusenter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class MigreringMediator(
    private val arenaRepository: ArenaRepository,
    private val migreringRepository: MigreringRepository,
    private val rapidsConnection: RapidsConnection,
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun startMigrering(
        sakId: Int,
        initiertAv: String,
        simulering: Boolean,
        migreringsTidspunkt: LocalDate,
    ): Migrering {
        arenaRepository.hentIdentForSak(sakId)
        val opplysninger = hentOpplysningerForSak(sakId, initiertAv)

        val migrering =
            Migrering(
                sakId = sakId,
                ident = arenaRepository.hentIdentForSak(sakId),
                initiertAv = initiertAv,
                simulering = simulering,
                migreringTidspunkt = migreringsTidspunkt,
            )
        migreringRepository.lagreMigrering(migrering)

        migreringRepository.lagreOpplysninger(
            migreringId = migrering.migreringId,
            opplysninger = opplysninger,
        )

        publiserStartMigrering(migrering)

        return migrering
    }

    internal fun håndter(
        melding: MigreringStartetMelding,
        context: MessageContext,
    ) {
        melding.lagreMelding(migreringRepository)
        val opplysninger =
            migreringRepository
                .hentManglendeOpplysningerForMigrering(melding.migreringId, melding.manglendeOpplysninger)
                .map { it.opplysning }

        val manglendeOpplysninger =
            melding.manglendeOpplysninger.toSet() - opplysninger.map { it.uuid }.toSet()
        if (manglendeOpplysninger.isNotEmpty()) {
            logger.warn {
                "Migrering ${melding.migreringId} stoppes fordi følgende opplysninger mangler i databasen: $manglendeOpplysninger"
            }
            return
        }

        melding.publiserManglendeOpplysninger(opplysninger) { json ->
            context.publish(json)
        }

        migreringRepository.markereOpplysningerSomMigrert(
            migreringId = melding.migreringId,
            opplysninger = opplysninger,
        )
    }

    internal fun håndter(melding: BehandlingOpprettetMelding) {
        melding.lagreMelding(migreringRepository)
        migreringRepository.markereBehandlingOpprettet(melding.migreringId, melding.behandlingId)
    }

    internal fun håndter(medling: ForslagTilBehandlingsresultatMelding) {
        medling.lagreMelding(migreringRepository)
    }

    private fun publiserStartMigrering(migrering: Migrering) {
        rapidsConnection.publish(
            JsonMessage
                .newMessage(
                    "migrering_startet",
                    mapOf(
                        "sakId" to migrering.sakId,
                        "migreringTidspunkt" to migrering.migreringTidspunkt.toString(),
                        "simulering" to migrering.simulering,
                        "initiertAv" to migrering.initiertAv,
                        "ident" to migrering.ident,
                        "migreringId" to migrering.migreringId.toString(),
                        "opplysninger" to emptyList<Any>(),
                    ),
                ).toJson(),
        )
    }

    private suspend fun hentOpplysningerForSak(
        sakId: Int,
        initiertAv: String,
    ): List<Opplysning<*>> =
        coroutineScope {
            val sakTilMigrering = arenaRepository.hentSakTilMigrering(sakId)
            val deferredOpplysninger =
                aktiveProdusenter.map { produsent ->
                    async {
                        try {
                            produsent.produser(
                                MigreringsKontekst(
                                    sakTilMigrering = sakTilMigrering,
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
