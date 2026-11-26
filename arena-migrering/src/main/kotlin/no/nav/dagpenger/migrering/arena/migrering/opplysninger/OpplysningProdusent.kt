package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import io.prometheus.metrics.core.metrics.Counter
import no.nav.dagpenger.behandling.api.models.OpplysningsverdiDTO
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.util.UUID

interface OpplysningProdusent<T : Any> {
    companion object {
        val logger = KotlinLogging.logger {}
        private val manglendeOpplysninger =
            Counter
                .builder()
                .name("arena_migrering_manglende_opplysninger_total")
                .help("Antall ganger en opplysning mangler under migrering fra Arena")
                .labelNames("id", "navn", "status")
                .register()
    }

    val opplysningsId: UUID
    val opplysningsNavn: String
    val aktiv: Boolean
    val rolle: Rolle

    val status: Status

    enum class Rolle {
        Fakta,
        Resultat,
    }

    suspend fun produser(kontekst: MigreringsKontekst): Opplysning<T>

    fun verifiser(
        verdi: T,
        // opplysning: Opplysning<Any>,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningVerifisertResultat {
        val arenaOpplysning =
            repository.hentOpplysning(
                migreringId = migreringId,
                opplysningId = opplysningsId,
            )
        val sammeVerdi = sammenlignVerdier(arenaOpplysning?.verdi, verdi)

        return OpplysningVerifisertResultat(
            uuid = opplysningsId,
            navn = opplysningsNavn,
            verifisert = sammeVerdi && arenaOpplysning?.uuid == opplysningsId,
            dpSakVerdi = verdi,
            arenaVerdi = arenaOpplysning?.verdi,
        )
    }

    fun sammenlignVerdier(
        arenaVerdi: Any?,
        dpSakVerdi: Any?,
    ): Boolean {
        listOf(arenaVerdi, dpSakVerdi).forEach { verdi ->
            when (verdi) {
                null, is Boolean, is String, is Int, is Long, is Double, is BigDecimal -> {
                }

                else -> {
                    throw UnsupportedOperationException(
                        "Opplysningen $opplysningsNavn ($opplysningsId) krever egen sammenligning for " +
                            "${verdi.javaClass.name}. Overstyr sammenlignVerdier.",
                    )
                }
            }
        }
        return arenaVerdi == dpSakVerdi
    }

    fun verifiserDto(
        verdi: OpplysningsverdiDTO,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningVerifisertResultat

    fun vedManglendeOpplysning() {
        manglendeOpplysninger.labelValues(opplysningsId.toString(), opplysningsNavn, status.name).inc()
        logger.info { "Fant ikke opplysning: $opplysningsNavn ($opplysningsId). Status: ${status.name}" }
    }

    enum class Status {
        Dummy,
        Alfa,
        Verifisert,
    }
}
