package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.objectMapper
import no.nav.dagpenger.uuid.UUIDv7
import tools.jackson.databind.DeserializationFeature
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal data class MigreringAvbruttMeldingV1(
    @param:JsonProperty("@id")
    @get:JsonProperty("@id")
    override val id: UUID,
    @param:JsonProperty("@event_name")
    @get:JsonProperty("@event_name")
    override val navn: String,
    @param:JsonProperty("@opprettet")
    @get:JsonProperty("@opprettet")
    override val opprettet: LocalDateTime,
    override val ident: String,
    override val migreringId: UUID,
    override val sakId: Int,
    override val initiertAv: String,
    override val migreringTidspunkt: LocalDate,
    override val simulering: Boolean,
    val årsak: Map<String, Any>,
) : MigreringMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Forventet eventnavn $EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "migrering_avbrutt"

        fun fra(
            kilde: MigreringMeldingV1,
            årsak: Map<String, Any>,
        ) = MigreringAvbruttMeldingV1(
            id = kilde.id,
            opprettet = kilde.opprettet,
            ident = kilde.ident,
            migreringId = kilde.migreringId,
            sakId = kilde.sakId,
            initiertAv = kilde.initiertAv,
            migreringTidspunkt = kilde.migreringTidspunkt,
            simulering = kilde.simulering,
            årsak = årsak,
            navn = EVENT_NAME,
        )

        fun fra(packet: JsonMessage): MigreringAvbruttMeldingV1 =
            objectMapper
                .readerFor(MigreringAvbruttMeldingV1::class.java)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(packet.toJson())

        fun ny(
            ident: String,
            migreringId: UUID,
            sakId: Int,
            initiertAv: String,
            migreringTidspunkt: LocalDate,
            simulering: Boolean,
            årsak: Map<String, Any>,
        ) = MigreringAvbruttMeldingV1(
            id = UUIDv7.ny(),
            opprettet = LocalDateTime.now(),
            ident = ident,
            migreringId = migreringId,
            sakId = sakId,
            initiertAv = initiertAv,
            migreringTidspunkt = migreringTidspunkt,
            simulering = simulering,
            årsak = årsak,
            navn = EVENT_NAME,
        )
    }
}
