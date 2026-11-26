package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OpplysningKontrollResultat
import no.nav.dagpenger.migrering.objectMapper
import tools.jackson.databind.DeserializationFeature
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class MigreringSimuleringKontrollResultatMeldingV1(
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
    val ok: List<OpplysningKontrollResultat>,
    val feilet: List<OpplysningKontrollResultat>,
) : MigreringMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Forventet eventnavn $EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "migrering_simulering_kontroll_resultat"

        fun fra(packet: JsonMessage): MigreringSimuleringKontrollResultatMeldingV1 =
            objectMapper
                .readerFor(MigreringSimuleringKontrollResultatMeldingV1::class.java)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(packet.toJson())

        fun fra(
            kilde: MigreringSimuleringResultatMeldingV1,
            ok: List<OpplysningKontrollResultat>,
            feilet: List<OpplysningKontrollResultat>,
        ): MigreringSimuleringKontrollResultatMeldingV1 =
            MigreringSimuleringKontrollResultatMeldingV1(
                id = UUID.randomUUID(),
                navn = EVENT_NAME,
                opprettet = LocalDateTime.now(),
                ident = kilde.ident,
                migreringId = kilde.migreringId,
                sakId = kilde.sakId,
                initiertAv = kilde.initiertAv,
                migreringTidspunkt = kilde.migreringTidspunkt,
                simulering = kilde.simulering,
                ok = ok,
                feilet = feilet,
            )
    }
}
