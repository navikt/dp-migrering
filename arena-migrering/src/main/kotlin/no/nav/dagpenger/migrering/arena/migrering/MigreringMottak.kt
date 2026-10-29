package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry

class MigreringMottak(
    rapidsConnection: RapidsConnection,
//    private val migreringRepository: MigreringRepository,
) : River.PacketListener {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    init {
        River(rapidsConnection)
            .apply {
                precondition {
                    it.requireAny(
                        "@event_name",
                        listOf(
                            "migrering_startet",
                            "migrering_feilet",
                            "behandling_opprettet",
                            "behandling_avbrutt",
                            "forslag_til_behandlingsresultat",
                        ),
                    )
                }
                validate {
                    it.interestedIn(
                        "sakId",
                        "ident",
                        "migreringId",
                        "initiertAv",
                        "opplysninger",
                        "arsak",
                        "@id",
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
        val eventName = packet["@event_name"].asString()
        val sakId = packet["sakId"].asInt()

        logger.info { "Mottok $eventName for sakId=$sakId" }

        // migreringRepository.lagre(packet)
    }
}
