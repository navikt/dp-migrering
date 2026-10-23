package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.nav.dagpenger.arena.migrering.api.models.OpplysningResponse
import no.nav.dagpenger.arena.migrering.api.models.StartMigreringResponse
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.OpplysningProdusent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class MigreringsOrchestrator(
    private val produsenter: List<OpplysningProdusent<*>>,
    private val repository: ArenaRepository,
    private val rapidsConnection: RapidsConnection,
) {
    suspend fun initierMigrering(
        sakId: Int,
        initiertAv: String,
    ): StartMigreringResponse {
        val opplysninger = hentOpplysningerForSak(sakId, initiertAv)
        val startMigreringMelding =
            lagStartMigreringMelding(
                sakId = sakId,
                initiertAv = initiertAv,
                opplysninger = opplysninger,
                ident = repository.hentIdentForSak(sakId),
            )

        rapidsConnection.publish(startMigreringMelding.toJson())
        return StartMigreringResponse(
            opplysninger =
                opplysninger.map {
                    OpplysningResponse(
                        navn = it.navn,
                        verdi = it.verdi.toString(),
                        gyldigFraOgMed = it.gyldigFraOgMed,
                        uuid = it.uuid.toString(),
                    )
                },
        )
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

    private fun lagStartMigreringMelding(
        sakId: Int,
        initiertAv: String,
        ident: String,
        opplysninger: List<Opplysning<*>>,
    ): JsonMessage {
        val message =
            JsonMessage.newMessage(
                "start_migrering",
                mapOf(
                    "sakId" to sakId,
                    "initiertAv" to initiertAv,
                    "opplysninger" to
                        opplysninger.map {
                            mapOf(
                                "verdi" to it.verdi,
                                "gyldigFraOgMed" to it.gyldigFraOgMed.toString(),
                                "uuid" to it.uuid.toString(),
                            )
                        },
                    "ident" to ident,
                    "innvilgetFraOgMed" to LocalDate.now(),
//                    "innvilgetTilOgMed" to null,
                ),
            )

        return message
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
