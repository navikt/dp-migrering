package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDateTime
import tools.jackson.databind.JsonNode
import java.util.UUID

internal abstract class KafkaMelding(
    private val packet: JsonMessage,
) {
    init {
        packet.interestedIn("@id", "@event_name", "@opprettet", "ident")
    }

    val id: UUID = packet["@id"].asUUID()
    val navn: String = packet["@event_name"].asString()
    val opprettet = packet["@opprettet"].asLocalDateTime()
    val ident: String = packet["ident"].asString()

    abstract val migreringId: UUID

    abstract fun hendelse(): Hendelse

    fun lagreMelding(repository: MigreringRepository) {
        repository.lagreMigreringHendelse(
            migreringId = migreringId,
            meldingId = id,
            meldingType = hendelse().type(),
            ident = ident,
            jsonData = toJson(),
        )
    }

    fun tracinginfo() =
        additionalTracinginfo(packet) +
            mapOf(
                "event_name" to navn,
                "id" to id.toString(),
                "opprettet" to opprettet.toString(),
            )

    protected open fun additionalTracinginfo(packet: JsonMessage): Map<String, String> = emptyMap()

    fun JsonNode.asUUID() = this.asString().let { UUID.fromString(it) }

    fun toJson() = packet.toJson()
}
