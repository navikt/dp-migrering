package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry
import no.nav.dagpenger.behandling.api.models.BehandlingsresultatDTO
import tools.jackson.databind.ObjectMapper

class MigreringMottak(
    rapidsConnection: RapidsConnection,
    private val objectMapper: ObjectMapper,
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
                        "behandlingId",
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
        val behandlingId = packet["behandlingId"].asStringOpt()?.get().let { it?.toUUID() }

        logger.info { "Mottok $eventName" }

        when (eventName) {
            "migrering_startet" -> {
                logger.info { "\tsakId=$sakId" }
            }

            "migrering_feilet" -> {
                logger.info { "\tsakId=$sakId" }
            }

            "behandling_opprettet" -> {
                logger.info { "\tmigreringId=$behandlingId" }
            }

            "behandling_avbrutt" -> {
                logger.info { "\tmigreringId=$behandlingId" }
            }

            "forslag_til_behandlingsresultat" -> {
                logger.info { "\tmigreringId=$behandlingId" }
                val behandlingsResultat = objectMapper.readValue(packet.toJson(), BehandlingsresultatDTO::class.java)
                logger.info { "\tmigreringResultat=$behandlingsResultat" }
            }
        }

        // migreringRepository.lagre(packet)
    }
}
