package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.BehandlingOpprettetMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.ForslagTilBehandlingsresultatMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.KafkaMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringAvbruttMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringManglendeOpplysningerMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringOpplysningerMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringSimuleringKontrollResultatMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringSimuleringResultatMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.meldinger.typet.MigreringStartetMeldingV1
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger.aktiveProdusenter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class MigreringMediator(
    private val arenaMigreringRepository: ArenaMigreringRepository,
    private val migreringRepository: MigreringRepository,
    private val rapid: RapidsConnection,
    private val migreringsScope: CoroutineScope,
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        private val tracer = GlobalOpenTelemetry.getTracer("arena-migrering")
    }

    @WithSpan("migrering.start")
    fun startMigrering(
        sakId: Int,
        initiertAv: String,
        simulering: Boolean,
        migreringsTidspunkt: LocalDate,
    ): Migrering {
        val migrering =
            Migrering(
                sakId = sakId,
                ident = arenaMigreringRepository.hentIdentForSak(sakId),
                initiertAv = initiertAv,
                simulering = simulering,
                migreringTidspunkt = migreringsTidspunkt,
            )
        migreringRepository.lagreMigrering(migrering)

        val feilhåndterer =
            CoroutineExceptionHandler { _, feil ->
                withLoggingContext("migreringId" to migrering.migreringId.toString()) {
                    logger.error { "Oppstart av migrering feilet: ${feil.javaClass.simpleName}" }
                }
            }

        migreringsScope.launch(Context.current().asContextElement() + feilhåndterer) {
            val opplysninger = hentOpplysningerForSak(sakId, initiertAv, migreringsTidspunkt.atStartOfDay())

            migreringRepository.lagreOpplysninger(
                migreringId = migrering.migreringId,
                opplysninger = opplysninger,
            )

            MigreringStartetMeldingV1.fra(migrering).publiser(context = rapid)
//            publiserMelding(
//                context = rapid,
//                melding = MigreringStartetMeldingV1.fra(migrering),
//            )
        }

        return migrering
    }

    @WithSpan("migrering.manglende_opplysninger")
    internal fun håndter(
        melding: MigreringManglendeOpplysningerMeldingV1,
        context: MessageContext,
    ) {
        melding.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Ekstern)

        val opplysninger =
            migreringRepository
                .hentManglendeOpplysningerForMigrering(
                    melding.migreringId,
                    melding.manglendeOpplysninger.map { it.uuid },
                ).map { it.opplysning }

        val opplysningsIderViHar = opplysninger.map { it.uuid }.toSet()
        val opplysningerViIkkeFinner =
            melding.manglendeOpplysninger.filterNot { it.uuid in opplysningsIderViHar }
        val viHarIkkeAlleNodvendigeOpplysninger = opplysningerViIkkeFinner.isNotEmpty()

        if (viHarIkkeAlleNodvendigeOpplysninger) {
            MigreringAvbruttMeldingV1
                .fra(
                    melding,
                    mapOf(
                        "kode" to "FINNER_IKKE_MANGLENDE_OPPLYSNINGER",
                        "beskrivelse" to opplysningerViIkkeFinner.joinToString { "${it.navn} (${it.uuid})" },
                    ),
                ).publiser(context)

            return
        }

        MigreringOpplysningerMeldingV1
            .fra(
                kilde = melding,
                opplysninger = opplysninger,
            ).also {
                it.publiser(context)
                it.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Intern)
                migreringRepository.markereOpplysningerSomMigrert(
                    migreringId = melding.migreringId,
                    opplysninger = opplysninger,
                )
            }
    }

    @WithSpan("migrering.simulering_resultat")
    internal fun håndter(
        melding: MigreringSimuleringResultatMeldingV1,
        context: MessageContext,
    ) {
        melding.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Ekstern)

        melding.kontroll(migreringRepository).also {
            logger.info { "Verifisert opplysninger (${it.verifisert.size}): ${it.verifisert.map { it.navn }}" }
            logger.warn { "Ikke verifisert opplysninger (${it.ikkeVerifisert.size}): ${it.ikkeVerifisert.map { it.navn }}" }
            context.publish(it.toJson())
        }
    }

    @WithSpan("migrering.startet")
    internal fun håndter(
        melding: MigreringStartetMeldingV1,
        context: MessageContext,
    ) {
        withLoggingContext("migreringId" to melding.migreringId.toString()) {
            logger.info { "Mottok start migrering melding" }
            melding.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Intern)
        }
    }

    @WithSpan("migrering.avbrutt")
    internal fun håndter(
        melding: MigreringAvbruttMeldingV1,
        context: MessageContext,
    ) {
        logger.warn {
            "Migrering ${melding.migreringId} stoppes fordi følgende opplysninger mangler i databasen: ${melding.årsak}"
        }
        melding.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Intern)
    }

    @WithSpan("migrering.behandling_opprettet")
    internal fun håndter(melding: BehandlingOpprettetMeldingV1) {
        melding.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Ekstern)
        migreringRepository.markereBehandlingOpprettet(melding.migreringId, melding.behandlingId)
    }

    @WithSpan("migrering.forslag_til_behandlingsresultat")
    internal fun håndter(medling: ForslagTilBehandlingsresultatMeldingV1) {
        medling.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Ekstern)
    }

    @WithSpan("migrering.simulering_kontroll_resultat")
    internal fun håndter(medling: MigreringSimuleringKontrollResultatMeldingV1) {
        medling.lagreMelding(migreringRepository, KafkaMeldingV1.KafkaMeldingOpprinnelse.Intern)
    }

    @WithSpan("migrering.hent_opplysninger")
    private suspend fun hentOpplysningerForSak(
        sakId: Int,
        initiertAv: String,
        migreringTidspunkt: LocalDateTime,
    ): List<Opplysning<*>> =
        coroutineScope {
            val sakTilMigrering = arenaMigreringRepository.hentSakTilMigrering(sakId, migreringTidspunkt)

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
