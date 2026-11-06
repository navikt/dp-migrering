package no.nav.dagpenger.migrering.arena.migrering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.toUUID
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysning
import no.nav.dagpenger.migrering.arena.migrering.opplysninger.Opplysninger.aktiveProdusenter
import tools.jackson.databind.node.NullNode
import java.time.LocalDateTime
import java.util.UUID

class MigreringMediator(
    private val arenaRepository: ArenaRepository,
    private val migreringRepository: MigreringRepository,
    private val rapidsConnection: RapidsConnection,
) {
    suspend fun startMigrering(
        sakId: Int,
        initiertAv: String,
        simulering: Boolean,
    ): Migrering {
        arenaRepository.hentIdentForSak(sakId)
        val opplysninger = hentOpplysningerForSak(sakId, initiertAv)

        val migrering =
            Migrering(
                sakId = sakId,
                ident = arenaRepository.hentIdentForSak(sakId),
                initiertAv = initiertAv,
                simulering = simulering,
            )
        migreringRepository.lagre(migrering)

        migreringRepository.lagreOpplysninger(
            migreringId = migrering.migreringId,
            opplysninger = opplysninger,
        )

        publiserStartMigrering(migrering)

        return migrering
    }

    fun svarManglendeOpplysninger(packet: JsonMessage) {
        val migreringId = packet["migreringId"].asString().toUUID()
        val manglendeOpplysninger = packet["manglendeOpplysninger"].asArray().toList().map { it.asString().toUUID() }

        val opplysninger =
            migreringRepository
                .hentManglendeOpplysningerForMigrering(migreringId, manglendeOpplysninger)
                .map { it.opplysning }

        publiserManglendeOpplysninger(packet, opplysninger)

        migreringRepository.markereOpplysningerSomMigrert(
            migreringId = UUID.fromString(packet["migreringId"].asString()),
            opplysninger = opplysninger,
        )
    }

    private fun publiserStartMigrering(migrering: Migrering) {
        rapidsConnection.publish(
            JsonMessage
                .newMessage(
                    "migrering_startet",
                    mapOf(
                        "sakId" to migrering.sakId,
                        "simulering" to migrering.simulering,
                        "initiertAv" to migrering.initiertAv,
                        "ident" to migrering.ident,
                        "migreringId" to migrering.migreringId.toString(),
                        "opplysninger" to emptyList<Any>(),
                    ),
                ).toJson(),
        )
    }

    private fun publiserManglendeOpplysninger(
        packet: JsonMessage,
        opplysninger: List<Opplysning<*>>,
    ) {
        rapidsConnection.publish(
            packet
                .also {
                    it["manglendeOpplysninger"] = NullNode.instance
                    it["opplysninger"] =
                        opplysninger.map { opplysning ->
                            mapOf(
                                "navn" to opplysning.navn,
                                "verdi" to opplysning.verdi,
                                "gyldigFraOgMed" to opplysning.gyldigFraOgMed?.toString(),
                                "uuid" to opplysning.uuid.toString(),
                            )
                        }
                }.toJson(),
        )
    }

    private suspend fun hentOpplysningerForSak(
        sakId: Int,
        initiertAv: String,
    ): List<Opplysning<*>> =
        coroutineScope {
            val sakTilMigrering = arenaRepository.hentSakTilMigrering(sakId)
            val deferredOpplysninger =
                aktiveProdusenter.map { produsent ->
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
}
