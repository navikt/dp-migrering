package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.behandling.api.models.OpplysningDTO
import no.nav.dagpenger.migrering.objectMapper
import no.nav.dagpenger.uuid.UUIDv7
import tools.jackson.databind.DeserializationFeature
import java.time.LocalDateTime
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class ForslagTilBehandlingsresultatMeldingV1(
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
    val behandlingId: UUID,
    val behandletHendelse: BehandletHendelse,
    val opplysninger: List<OpplysningDTO>,
) : BehandlingMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Forventet eventnavn $EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "forslag_til_behandlingsresultat"

        fun fra(packet: JsonMessage): ForslagTilBehandlingsresultatMeldingV1 =
            objectMapper
                .readerFor(ForslagTilBehandlingsresultatMeldingV1::class.java)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(packet.toJson())

        fun ny(
            ident: String,
            behandlingId: UUID,
            behandletHendelse: BehandletHendelse,
            opplysninger: List<OpplysningDTO>,
        ): ForslagTilBehandlingsresultatMeldingV1 =
            ForslagTilBehandlingsresultatMeldingV1(
                id = UUIDv7.ny(),
                navn = EVENT_NAME,
                opprettet = LocalDateTime.now(),
                ident = ident,
                behandlingId = behandlingId,
                behandletHendelse = behandletHendelse,
                opplysninger = opplysninger,
            )
    }

    override val migreringId: UUID
        get() = behandletHendelse.id

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class BehandletHendelse(
        val id: UUID,
        val type: String,
    )
}
