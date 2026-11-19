package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.OutgoingMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import io.prometheus.metrics.core.metrics.Counter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.nav.dagpenger.migrering.arena.migrering.meldinger.BehandlingOpprettetMelding
import no.nav.dagpenger.migrering.arena.migrering.meldinger.ForslagTilBehandlingsresultatMelding
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

        private val manglendeOpplysninger =
            Counter
                .builder()
                .name("migrering_manglende_opplysninger_forespurt_total")
                .help("Antall ganger en opplysning mangler blitt forespurt under migrering")
                .labelNames("id", "navn")
                .register()
    }

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

    internal fun håndter(
        melding: MigreringSimuleringResultatMelding,
        context: MessageContext,
    ) {
        melding.lagreMelding(migreringRepository)

        withLoggingContext("migreringId" to melding.migreringId.toString()) {
            logger.info { "Mottok simulering resultat for migrering. Resultat: ${melding.simuleringResultat}" }
        }
    }

    internal fun håndter(
        melding: MigreringStartetMelding,
        context: MessageContext,
    ) {
        withLoggingContext("migreringId" to melding.migreringId.toString()) {
            logger.info { "Mottok start migrering melding" }
            melding.lagreMelding(migreringRepository)
        }
    }

    internal fun håndter(
        melding: MigreringAvbruttMelding,
        context: MessageContext,
    ) {
        logger.warn {
            "Migrering ${melding.migreringId} stoppes fordi følgende opplysninger mangler i databasen: ${melding.årsak}"
        }
        melding.lagreMelding(migreringRepository)
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
        migreringTidspunkt: LocalDateTime,
    ): List<Opplysning<*>> =
        coroutineScope {
            val sakTilMigrering = arenaMigreringRepository.hentSakTilMigrering(sakId)
            val deferredOpplysninger =
                aktiveProdusenter.map { produsent ->
                    async {
                        try {
                            produsent.produser(
                                MigreringsKontekst(
                                    sakTilMigrering = sakTilMigrering,
                                    migreringsId = UUID.randomUUID(),
                                    initiertAv = initiertAv,
                                    opprettetTidspunkt = migreringTidspunkt,
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
    ) {
        val (sendte, feilede) = context.publish(listOf(OutgoingMessage(melding.toJson())))
        if (feilede.isNotEmpty()) {
            throw IllegalStateException("Kunne ikke publisere migreringshendelse", feilede.first().error)
        }
        val publisertJson = sendte.single().message.body
//        val publisertMelding = JsonMessage(publisertJson, MessageProblems(publisertJson))
        // melding.lagreMelding(migreringRepository)
//        migreringRepository.lagreMigreringHendelse(
//            migreringId = melding.migreringId,
//            meldingId = UUID.fromString(publisertMelding.id),
//            hendelse = melding.hendelse.navn(),
//            ident = melding.ident,
//            jsonData = publisertJson,
//            opprinnelse = melding.hendelse.opprinnelse().name,
//        )
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
