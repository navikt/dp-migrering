package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.DeserializationFeature
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal data class MigreringManglendeOpplysningerMeldingV1(
    @param:JsonProperty("@id")
    @get:JsonProperty("@id")
    override val id: UUID,
    @param:JsonProperty("@event_name")
    @get:JsonProperty("@event_name")
    override val navn: String,
    @param:JsonProperty("@opprettet")
    @get:JsonProperty("@opprettet")
    override val opprettet: LocalDateTime,
    override val migreringId: UUID,
    override val sakId: Int,
    override val initiertAv: String,
    override val migreringTidspunkt: LocalDate,
    override val simulering: Boolean,
    override val ident: String,
    val manglendeOpplysninger: List<Opplysning<Any>>,
    val opplysninger: List<Opplysning<Any>>,
) : MigreringMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Forventet eventnavn $EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "migrering_manglende_opplysninger"

        fun fra(packet: JsonMessage): MigreringManglendeOpplysningerMeldingV1 =
            objectMapper
                .readerFor(MigreringManglendeOpplysningerMeldingV1::class.java)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(packet.toJson())
    }
}
