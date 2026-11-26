package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.arena.migrering.Migrering
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.DeserializationFeature
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class MigreringStartetMeldingV1(
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
    override val simulering: Boolean = false,
    val opplysninger: List<Opplysning<*>>,
) : MigreringMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Forventet eventnavn $EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "migrering_startet"

        fun fra(migrering: Migrering): MigreringStartetMeldingV1 =
            MigreringStartetMeldingV1(
                id = UUID.randomUUID(),
                navn = EVENT_NAME,
                opprettet = LocalDateTime.now(),
                ident = migrering.ident,
                migreringId = migrering.migreringId,
                sakId = migrering.sakId,
                initiertAv = migrering.initiertAv,
                migreringTidspunkt = migrering.migreringTidspunkt,
                simulering = migrering.simulering,
                opplysninger = emptyList(),
            )

        fun fra(packet: JsonMessage): MigreringStartetMeldingV1 =
            objectMapper
                .readerFor(MigreringStartetMeldingV1::class.java)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(packet.toJson())
    }
}
