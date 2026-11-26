package no.nav.dagpenger.migrering.arena.migrering.mottak

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import io.micrometer.core.instrument.MeterRegistry
import no.nav.dagpenger.migrering.arena.migrering.MigreringMediator
import no.nav.dagpenger.migrering.arena.migrering.meldinger.MigreringAvbruttMeldingV1
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.unleash

class MigreringAvbruttMottak(
    rapidsConnection: RapidsConnection,
    private val migreringMediator: MigreringMediator,
) : River.PacketListener {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    init {
        River(rapidsConnection)
            .apply {
                precondition { it.requireValue("@event_name", MigreringAvbruttMeldingV1.EVENT_NAME) }
                validate {
                    it.requireKey(
                        "migreringId",
                        "@id",
                        "ident",
                        "årsak",
                    )
                }
            }.register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
        metadata: MessageMetadata,
        meterRegistry: MeterRegistry,
    ) {
        if (unleash.isEnabled(Feature.START_MIGRERING.navn)) {
            withLoggingContext(
                "migreringId" to packet["migreringId"].asString(),
            ) {
                logger.info { "Mottok melding om avbrutt migrering" }
                migreringMediator.håndter(
                    MigreringAvbruttMeldingV1.fra(packet),
                    context,
                )
            }
        }
    }

    override fun onError(
        problems: MessageProblems,
        context: MessageContext,
        metadata: MessageMetadata,
    ) {
        logger.info { "Feil i melding om avbrutt migrering: ${problems.toExtendedReport()}" }
    }
}
