package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.MeterRegistry

class MigrationRiver(
    rapidsConnection: RapidsConnection,
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
                            "start_migrering",
                            "teknisk_migrering_fullfort",
                            "migrering_avbrutt",
                        ),
                    )
                }
                validate {
                    it.interestedIn(
                        "sakId",
                        "initiertAv",
                        "opplysninger",
                        "arsak",
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
        when (val eventName = packet["@event_name"].asString()) {
            "start_migrering" -> {
                val sakId = packet["sakId"].asInt()
                val initiertAv = packet["initiertAv"].asString()
                val opplysninger = packet["opplysninger"].asArray()
                val arsak = packet["arsak"].asString()

                logger.info { "Mottok start-migrering for sakId=$sakId, initiertAv=$initiertAv, arsak=$arsak" }
                logger.debug { "Opplysninger: $opplysninger" }
            }

            "teknisk_migrering_fullfort" -> {
                val sakId = packet["sakId"].asInt()
                logger.info { "Mottok teknisk_migrering_fullfort for sakId=$sakId" }
            }

            "migrering_avbrutt" -> {
                val sakId = packet["sakId"].asInt()
                val arsak = packet["arsak"]
                logger.info { "Mottok migrering_avbrutt for sakId=$sakId, arsak=$arsak" }
            }

            else -> {
                logger.warn { "Ukjent event_name: $eventName" }
            }
        }
    }
}
