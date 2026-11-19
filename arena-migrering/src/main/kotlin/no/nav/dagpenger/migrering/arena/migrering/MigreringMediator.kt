package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.OutgoingMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import io.opentelemetry.instrumentation.annotations.WithSpan
import io.prometheus.metrics.core.metrics.Counter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import no.nav.dagpenger.migrering.arena.migrering.meldinger.BehandlingOpprettetMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.ForslagTilBehandlingsresultatMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.KafkaMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringAvbruttMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringManglendeOpplysningerMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringOpplysningerMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringSimuleringResultatMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringStartetMelding
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger.aktiveProdusenter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class MigreringMediator(
    private val arenaMigreringRepository: ArenaMigreringRepository,
    private val migreringRepository: MigreringRepository,
    private val rapid: RapidsConnection,
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        private val tracer = GlobalOpenTelemetry.getTracer("arena-migrering")

        private val manglendeOpplysninger =
            Counter
                .builder()
                .name("migrering_manglende_opplysninger_forespurt_total")
                .help("Antall ganger en opplysning mangler blitt forespurt under migrering")
                .labelNames("id", "navn")
                .register()
    }

    @WithSpan("migrering.start")
    suspend fun startMigrering(
        sakId: Int,
        initiertAv: String,
        simulering: Boolean,
        migreringsTidspunkt: LocalDate,
    ): Migrering {
        val opplysninger = hentOpplysningerForSak(sakId, initiertAv, migreringsTidspunkt.atStartOfDay())

        val migrering =
            Migrering(
                sakId = sakId,
                ident = arenaMigreringRepository.hentIdentForSak(sakId),
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

    @WithSpan("migrering.manglende_opplysninger")
    internal fun håndter(
        melding: MigreringManglendeOpplysningerMelding,
        context: MessageContext,
    ) {
        melding.lagreMelding(migreringRepository)

        val opplysninger =
            migreringRepository
                .hentManglendeOpplysningerForMigrering(
                    melding.migreringId,
                    melding.manglendeOpplysninger().map { it.uuid },
                ).map { it.opplysning }

        val opplysningsIderViHar = opplysninger.map { it.uuid }.toSet()
        val opplysningerViIkkeFinner =
            melding.manglendeOpplysninger().filterNot { it.uuid in opplysningsIderViHar }
        val viHarIkkeAlleNodvendigeOpplysninger = opplysningerViIkkeFinner.isNotEmpty()

        if (viHarIkkeAlleNodvendigeOpplysninger) {
            MigreringAvbruttMelding
                .ny(
                    migreringId = melding.migreringId,
                    sakId = melding.sakId,
                    ident = melding.ident,
                    initiertAv = melding.initiertAv,
                    årsak =
                        mapOf(
                            "kode" to "FINNER_IKKE_MANGLENDE_OPPLYSNINGER",
                            "beskrivelse" to opplysningerViIkkeFinner.joinToString { "${it.navn} (${it.uuid})" },
                        ),
                ).also {
                    rapid.publish(it.toJson())
                }

            return
        }

        MigreringOpplysningerMelding
            .fra(
                migreringId = melding.migreringId,
                sakId = melding.sakId,
                initiertAv = melding.initiertAv,
                ident = melding.ident,
                migreringTidspunkt = melding.migreringTidspunkt,
                opplysninger = opplysninger,
                simulering = melding.simulering,
            ).also {
                publiserOgLagreMelding(
                    context = context,
                    melding = it,
                )
                it.lagreMelding(migreringRepository)
                migreringRepository.markereOpplysningerSomMigrert(
                    migreringId = melding.migreringId,
                    opplysninger = opplysninger,
                )
            }
    }

    @WithSpan("migrering.simulering_resultat")
    internal fun håndter(
        melding: MigreringSimuleringResultatMelding,
        context: MessageContext,
    ) {
        melding.lagreMelding(migreringRepository)

        withLoggingContext("migreringId" to melding.migreringId.toString()) {
            logger.info { "Mottok simulering resultat for migrering. Resultat: ${melding.simuleringResultat}" }
        }
    }

    @WithSpan("migrering.startet")
    internal fun håndter(
        melding: MigreringStartetMelding,
        context: MessageContext,
    ) {
        withLoggingContext("migreringId" to melding.migreringId.toString()) {
            logger.info { "Mottok start migrering melding" }
            melding.lagreMelding(migreringRepository)
        }
    }

    @WithSpan("migrering.avbrutt")
    internal fun håndter(
        melding: MigreringAvbruttMelding,
        context: MessageContext,
    ) {
        logger.warn {
            "Migrering ${melding.migreringId} stoppes fordi følgende opplysninger mangler i databasen: ${melding.årsak}"
        }
        melding.lagreMelding(migreringRepository)
    }

    @WithSpan("migrering.behandling_opprettet")
    internal fun håndter(melding: BehandlingOpprettetMelding) {
        melding.lagreMelding(migreringRepository)
        migreringRepository.markereBehandlingOpprettet(melding.migreringId, melding.behandlingId)
    }

    @WithSpan("migrering.forslag_til_behandlingsresultat")
    internal fun håndter(medling: ForslagTilBehandlingsresultatMelding) {
        medling.lagreMelding(migreringRepository)
    }

    @WithSpan("migrering.hent_opplysninger")
    private suspend fun hentOpplysningerForSak(
        sakId: Int,
        initiertAv: String,
        migreringTidspunkt: LocalDateTime,
    ): List<Opplysning<*>> =
        coroutineScope {
            val sakTilMigrering = arenaMigreringRepository.hentSakTilMigrering(sakId)

            val parentContext = Context.current()
            val deferredOpplysninger =
                aktiveProdusenter.map { produsent ->
                    async {
                        val span =
                            tracer
                                .spanBuilder("migrering.produser_opplysning")
                                .setParent(parentContext)
                                .setAttribute(
                                    "migrering.opplysning_id",
                                    produsent.opplysningsId.toString(),
                                ).setAttribute(
                                    "migrering.opplysning_navn",
                                    produsent.opplysningsNavn,
                                ).startSpan()
                        try {
                            withContext(parentContext.with(span).asContextElement()) {
                                produsent.produser(
                                    MigreringsKontekst(
                                        sakTilMigrering = sakTilMigrering,
                                        migreringsId = UUID.randomUUID(),
                                        initiertAv = initiertAv,
                                        opprettetTidspunkt = migreringTidspunkt,
                                    ),
                                )
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            span.setStatus(StatusCode.ERROR, "Opplysningsprodusenten feilet")
                            throw MigreringsException("Feilet i ${produsent.opplysningsId}", e)
                        } finally {
                            span.end()
                        }
                    }
                }

            deferredOpplysninger.awaitAll()
        }

    private fun publiserOgLagreMelding(
        context: MessageContext,
        melding: KafkaMelding,
    ) {
        val (sendte, feilede) = context.publish(listOf(OutgoingMessage(melding.toJson())))
        if (feilede.isNotEmpty()) {
            throw IllegalStateException("Kunne ikke publisere migreringshendelse", feilede.first().error)
        }
    }

    fun hentMigrering(migreringId: UUID): Migrering? = migreringRepository.hentMigrering(migreringId)

    fun hentOppdateringer(
        migreringId: UUID,
        lastEventId: Long,
        limit: Int,
    ): List<MigreringHendelse> {
        require(lastEventId >= 0) { "Cursor må være 0 eller større" }
        require(limit in 1..100) { "Limit må være mellom 1 og 100" }
        return migreringRepository.migreringHendelser(migreringId, lastEventId, limit)
    }

    fun hentMigreringHendelse(hendelseId: Long): MigreringHendelse? = migreringRepository.hentMigreringHendels(hendelseId)

    fun hentMigreringerForSak(sakId: Int): List<Migrering> = migreringRepository.hentMigreringerForSak(sakId)
}
