package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.OutgoingMessage
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
    private val rapid: RapidsConnection,
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

        val melding = MigreringStartetMelding.fra(migrering)
        publiserOgLagreMelding(
            context = rapid,
            melding = melding,
        )

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
            MigreringAvbrutt
                .ny(
                    migreringId = melding.migreringId,
                    sakId = melding.sakId,
                    initiertAv = "ukjent",
                    ident = melding.ident,
                    årsak = mapOf("manglendeOpplysninger" to manglendeOpplysninger.toList()),
                ).also {
                    håndter(it, context)
                }

            return
        }

        val svar = melding.svar(opplysninger)
        publiserOgLagreMelding(
            context = context,
            melding = svar,
            hendelse = melding.hendelse.navn(),
        )

        migreringRepository.markereOpplysningerSomMigrert(
            migreringId = melding.migreringId,
            opplysninger = opplysninger,
        )
    }

    internal fun håndter(
        melding: MigreringAvbrutt,
        context: MessageContext,
    ) {
        logger.warn {
            "Migrering ${melding.migreringId} stoppes fordi følgende opplysninger mangler i databasen: ${melding.årsak}"
        }
        publiserOgLagreMelding(
            context = context,
            melding = melding,
        )
    }

    internal fun håndter(melding: BehandlingOpprettetMelding) {
        melding.lagreMelding(migreringRepository)
        migreringRepository.markereBehandlingOpprettet(melding.migreringId, melding.behandlingId)
    }

    internal fun håndter(medling: ForslagTilBehandlingsresultatMelding) {
        medling.lagreMelding(migreringRepository)
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

    private fun publiserOgLagreMelding(
        context: MessageContext,
        melding: KafkaMelding,
        hendelse: String = melding.hendelse.navn(),
    ) {
        val (sendte, feilede) = context.publish(listOf(OutgoingMessage(melding.toJson())))
        if (feilede.isNotEmpty()) {
            throw IllegalStateException("Kunne ikke publisere migreringshendelse", feilede.first().error)
        }
        val publisertJson = sendte.single().message.body
        val publisertMelding = JsonMessage(publisertJson, MessageProblems(publisertJson))
        migreringRepository.lagreMigreringHendelse(
            migreringId = melding.migreringId,
            meldingId = UUID.fromString(publisertMelding.id),
            hendelse = hendelse,
            ident = melding.ident,
            jsonData = publisertJson,
            opprinnelse = melding.hendelse.opprinnelse().name,
        )
    }
}
