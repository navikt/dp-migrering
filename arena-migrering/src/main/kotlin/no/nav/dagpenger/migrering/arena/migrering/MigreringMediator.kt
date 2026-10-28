package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.nav.dagpenger.arena.migrering.api.models.MigreringGrunnlagDTO
import no.nav.dagpenger.arena.migrering.api.models.MigreringGrunnlagOpplysningDTO
import no.nav.dagpenger.arena.migrering.api.models.MigreringGrunnlagOpplysningVerdiDTO
import no.nav.dagpenger.arena.migrering.api.models.MigreringGrunnlagOpplysningVerdiDTOTypeDTO
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringDTO
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OpplysningProdusent
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class MigreringMediator(
    private val produsenter: List<OpplysningProdusent<*>>,
    private val repository: ArenaRepository,
    private val rapidsConnection: RapidsConnection,
    private val objectMapper: ObjectMapper,
) {
    suspend fun hentMigreringGrunnlag(
        sakId: Int,
        initiertAv: String,
    ): MigreringGrunnlagDTO {
        val opplysninger = hentOpplysningerForSak(sakId, initiertAv)

        return MigreringGrunnlagDTO(
            sakId = sakId,
            initiertAv = initiertAv,
            ident = repository.hentIdentForSak(sakId),
            opplysninger =
                opplysninger.map {
                    MigreringGrunnlagOpplysningDTO(
                        navn = it.navn,
                        verdi =
                            MigreringGrunnlagOpplysningVerdiDTO(
                                verdi = it.verdi,
                                type = it.toVerdiDTOType(),
                            ),
                        gyldigFraOgMed = it.gyldigFraOgMed,
                        uuid = it.uuid,
                    )
                },
            innvilgetFraOgMed = LocalDate.now(),
            innvilgetTilOgMed = null,
        )
    }

    fun startMigrering(startMigreringDTO: StartMigreringDTO) {
        rapidsConnection.publish(toJsonMessage("migrering_startet", startMigreringDTO).toJson())
    }

    private suspend fun hentOpplysningerForSak(
        sakId: Int,
        initiertAv: String,
    ): List<Opplysning<*>> =
        coroutineScope {
            val sakTilMigrering = repository.hentSakTilMigrering(sakId)
            val deferredOpplysninger =
                produsenter.map { produsent ->
                    async {
                        try {
                            produsent.produser(
                                MigreringsKontekst(
                                    sakTilMigrering = sakTilMigrering,
                                    migreringsId = UUID.randomUUID(),
                                    initiertAv = initiertAv,
                                    opprettetTidspunkt = LocalDateTime.now(),
                                ),
                            )
                        } catch (e: Exception) {
                            throw MigreringsException("Feilet i ${produsent.opplysningsId}", e)
                        }
                    }
                }

            deferredOpplysninger.awaitAll()
        }

    fun toJsonMessage(
        eventName: String,
        dto: Any,
    ): JsonMessage {
        val node = objectMapper.valueToTree<ObjectNode>(dto)
        node.put("@event_name", eventName)
        val json = objectMapper.writeValueAsString(node)
        return JsonMessage(json, MessageProblems(json))
    }
}

fun Opplysning<*>.toVerdiDTOType(): MigreringGrunnlagOpplysningVerdiDTOTypeDTO =
    when (verdiType) {
        String::class -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.STRING
        }

        Byte::class, Short::class, Int::class, Long::class -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.INTEGER
        }

        Float::class, Double::class, java.math.BigDecimal::class -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.FLOAT
        }

        Boolean::class -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.BOOLEAN
        }

        LocalDate::class -> {
            MigreringGrunnlagOpplysningVerdiDTOTypeDTO.DATE
        }

        else -> {
            error("Ustøttet verditype for opplysning '$navn': $verdiType")
        }
    }

// fun JsonMessage.toStartMigreringResponse(): StartMigreringResponse {
//    val opplysninger = this["opplysninger"].asArray().toList().map { it.toOpplysningResponse() }
//
//    return StartMigreringResponse(
//        opplysninger = opplysninger,
//    )
// }
//
// private fun JsonNode.toOpplysningResponse(): OpplysningResponse =
//    OpplysningResponse(
//        navn = this["navn"].asString(),
//        verdi = this["verdi"].asText(),
//        gyldigFraOgMed = this["gyldigFraOgMed"]?.asText()?.let { LocalDate.parse(it) },
//        uuid = this["uuid"].asString(),
//    )
