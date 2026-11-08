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

class BehandlingOpprettetMottak(
    rapidsConnection: RapidsConnection,
    private val migreringMediator: MigreringMediator,
) : River.PacketListener {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    init {
        River(rapidsConnection)
            .apply {
                precondition {
                    it.requireValue("@event_name", "behandling_opprettet")
                    it.requireValue("behandletHendelse.type", "Migrering")
                }
                validate {
                    it.requireKey(
                        "behandletHendelse",
                        "behandletHendelse.id",
                        "behandlingId",
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
                "behandlingId" to packet["behandlingId"].asString(),
                "migreringId" to packet["behandletHendelse"]["id"].asString(),
            ) {
                logger.info { "Mottok melding om behandling opprettet for migrering" }
                migreringMediator.vedBehandlingOpprettet(packet)
            }
        }
    }
}
