package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.uuid.UUIDv7
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal data class MigreringOpplysningerMeldingV1(
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
    val opplysninger: List<Opplysning<*>>,
) : MigreringMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Forventet eventnavn $EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "migrering_opplysninger"

        fun fra(
            kilde: MigreringManglendeOpplysningerMeldingV1,
            opplysninger: List<Opplysning<*>>,
        ): MigreringOpplysningerMeldingV1 =
            MigreringOpplysningerMeldingV1(
                id = UUIDv7.ny(),
                navn = EVENT_NAME,
                opprettet = kilde.opprettet,
                migreringId = kilde.migreringId,
                sakId = kilde.sakId,
                initiertAv = kilde.initiertAv,
                migreringTidspunkt = kilde.migreringTidspunkt,
                simulering = kilde.simulering,
                ident = kilde.ident,
                opplysninger = opplysninger,
            )
    }
}
