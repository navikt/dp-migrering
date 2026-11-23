package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.DeserializationFeature

internal object KafkaMeldingJson {
    fun skriv(melding: KafkaMeldingV1): JsonMessage {
        val json = objectMapper.writeValueAsString(melding)
        return JsonMessage(json, MessageProblems(json))
    }

    fun lesStartet(packet: JsonMessage): MigreringStartetMeldingV1 = les(packet, MigreringStartetMeldingV1::class.java)

    fun lesKontrollresultat(packet: JsonMessage): MigreringSimuleringKontrollResultatMeldingV1 =
        les(packet, MigreringSimuleringKontrollResultatMeldingV1::class.java)

    fun lesBehandlingOpprettet(packet: JsonMessage): BehandlingOpprettetMeldingV1 = les(packet, BehandlingOpprettetMeldingV1::class.java)

    inline fun <reified T : KafkaMeldingV1> fra(packet: JsonMessage): MottattMelding<T> =
        MottattMelding(
            objectMapper
                .readerFor(T::class.java)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue<T>(packet.toJson()),
            packet,
        )

    private fun <T : KafkaMeldingV1> les(
        packet: JsonMessage,
        type: Class<T>,
    ): T =
        objectMapper
            .readerFor(type)
            .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .readValue<T>(packet.toJson())
}
