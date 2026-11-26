package no.nav.dagpenger.migrering.arena.migrering.meldinger

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.behandling.api.models.OpplysningerDTO
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger.produsentMap
import no.nav.dagpenger.migrering.objectMapper
import no.nav.dagpenger.uuid.UUIDv7
import tools.jackson.databind.DeserializationFeature
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
internal class MigreringSimuleringResultatMeldingV1(
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
    val simulerteOpplysninger: List<OpplysningerDTO>,
) : MigreringMeldingV1 {
    init {
        require(navn == EVENT_NAME) { "Melding må ha event_name=$EVENT_NAME" }
    }

    companion object {
        const val EVENT_NAME = "migrering_simulering_resultat"

        fun fra(packet: JsonMessage): MigreringSimuleringResultatMeldingV1 {
            val melding =
                objectMapper
                    .readerFor(MigreringSimuleringResultatMeldingV1::class.java)
                    .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .readValue(packet.toJson()) as MigreringSimuleringResultatMeldingV1
            return melding
        }

        fun ny(
            migreringId: UUID,
            sakId: Int,
            initiertAv: String,
            migreringTidspunkt: LocalDate,
            ident: String,
            simulering: Boolean,
            simulerteOpplysninger: List<OpplysningerDTO>,
        ): MigreringSimuleringResultatMeldingV1 =
            MigreringSimuleringResultatMeldingV1(
                id = UUIDv7.ny(),
                navn = EVENT_NAME,
                migreringId = migreringId,
                sakId = sakId,
                initiertAv = initiertAv,
                migreringTidspunkt = migreringTidspunkt,
                simulering = simulering,
                opprettet = LocalDateTime.now(),
                ident = ident,
                simulerteOpplysninger = simulerteOpplysninger,
            )
    }

    fun kontroll(migreringRepository: MigreringRepository): MigreringSimuleringKontrollResultatMeldingV1 {
        val (verifisert, ikkeVerifisert) =
            simulerteOpplysninger
                .mapNotNull { opplysning ->
                    val produsent = produsentMap[opplysning.opplysningTypeId] ?: return@mapNotNull null
                    val verdi = opplysning.perioder.first().verdi

                    produsent.kontrollerResultat(
                        verdi = verdi,
                        migreringId = migreringId,
                        repository = migreringRepository,
                    )
                }.partition { it.ok }

        return MigreringSimuleringKontrollResultatMeldingV1.fra(
            kilde = this,
            ok = verifisert,
            feilet = ikkeVerifisert,
        )
    }
}
