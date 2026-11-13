package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import io.micrometer.core.instrument.MeterRegistry
import no.nav.dagpenger.migrering.konfigurasjon.Feature
import no.nav.dagpenger.migrering.konfigurasjon.unleash

class StartMigreringMottak(
    rapidsConnection: RapidsConnection,
    private val migreringMediator: MigreringMediator,
) : River.PacketListener {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    init {
        River(rapidsConnection)
            .apply {
                precondition { it.requireValue("@event_name", "migrering_startet") }
                validate {
                    it.requireKey(
                        "manglendeOpplysninger",
                        "migreringId",
                        "@id",
                        "ident",
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
                logger.info { "Mottok melding om manglende opplysninger for migrering" }
                migreringMediator.håndter(
                    MigreringStartetMelding(packet, Hendelse.Opprinnelse.Ekstern, "MIGRERING_STARTET_MANGLENDE_OPPLYSNINGER"),
                    context,
                )
            }
        }
    }
}
