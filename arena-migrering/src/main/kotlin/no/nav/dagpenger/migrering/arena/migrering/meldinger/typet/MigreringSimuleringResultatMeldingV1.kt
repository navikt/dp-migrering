package no.nav.dagpenger.migrering.arena.migrering.meldinger.typet

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OpplysningProdusent
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger.produsentMap
import no.nav.dagpenger.migrering.objectMapper
import no.nav.dagpenger.uuid.UUIDv7
import tools.jackson.databind.DeserializationFeature
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
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
    val simulerteOpplysninger: List<Opplysning<Any>>,
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
            return melding.withTypedOpplysninger()
        }

        fun ny(
            migreringId: UUID,
            sakId: Int,
            initiertAv: String,
            migreringTidspunkt: LocalDate,
            ident: String,
            simulering: Boolean,
            simulerteOpplysninger: List<Opplysning<Any>>,
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

    private fun withTypedOpplysninger(): MigreringSimuleringResultatMeldingV1 =
        MigreringSimuleringResultatMeldingV1(
            id = id,
            navn = navn,
            opprettet = opprettet,
            migreringId = migreringId,
            sakId = sakId,
            initiertAv = initiertAv,
            migreringTidspunkt = migreringTidspunkt,
            simulering = simulering,
            ident = ident,
            simulerteOpplysninger =
                simulerteOpplysninger.map { opplysning ->
                    val produsent = produsentMap[opplysning.uuid] ?: return@map opplysning
                    val verdiType = produsent.verdiType() ?: return@map opplysning
                    if (opplysning.verdi == null || verdiType.isInstance(opplysning.verdi)) {
                        opplysning
                    } else {
                        opplysning.copy(verdi = objectMapper.convertValue(opplysning.verdi, verdiType))
                    }
                },
        )

    private fun OpplysningProdusent<*>.verdiType(): Class<*>? =
        javaClass.genericInterfaces
            .asSequence()
            .mapNotNull { it as? ParameterizedType }
            .firstOrNull { (it.rawType as? Class<*>) == OpplysningProdusent::class.java }
            ?.actualTypeArguments
            ?.firstOrNull()
            ?.toClass()

    private fun Type.toClass(): Class<*>? =
        when (this) {
            is Class<*> -> this
            is ParameterizedType -> rawType as? Class<*>
            else -> null
        }

    fun kontroll(migreringRepository: MigreringRepository): MigreringSimuleringKontrollResultatMeldingV1 {
        val (verifisert, ikkeVerifisert) =
            simulerteOpplysninger
                .mapNotNull {
                    produsentMap[it.uuid]?.verifiser(it, migreringId, migreringRepository)
                }.partition { it.verifisert }

        return MigreringSimuleringKontrollResultatMeldingV1.fra(
            kilde = this,
            verifisert = verifisert,
            ikkeVerifisert = ikkeVerifisert,
        )
    }
}
