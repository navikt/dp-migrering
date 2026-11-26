package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import io.prometheus.metrics.core.metrics.Counter
import no.nav.dagpenger.behandling.api.models.OpplysningsverdiDTO
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.reflect.KClass

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
    val verdiType: KClass<T>
    val aktiv: Boolean
    val rolle: Rolle

    val status: Status

    enum class Rolle {
        Fakta,
        Resultat,
    }

    suspend fun produser(kontekst: MigreringsKontekst): Opplysning<T>

    fun kontroller(
        verdi: T,
        // opplysning: Opplysning<Any>,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningKontrollResultat {
        val arenaOpplysning =
            repository.hentOpplysning(
                migreringId = migreringId,
                opplysningId = opplysningsId,
                verdiType = verdiType,
            )
        val sammeVerdi = sammenlignVerdier(arenaOpplysning?.verdi, verdi)

        return OpplysningKontrollResultat(
            uuid = opplysningsId,
            navn = opplysningsNavn,
            ok = sammeVerdi && arenaOpplysning?.uuid == opplysningsId,
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
                null, is Boolean, is String, is Int, is Long, is Double, is BigDecimal, is LocalDate -> {
                }

                else -> {
                    throw UnsupportedOperationException(
                        "Opplysningen $opplysningsNavn ($opplysningsId) krever egen sammenligning for " +
                            "${verdi.javaClass.name}. Overstyr sammenlignVerdier.",
                    )
                }
            }
        }
        return if (arenaVerdi is BigDecimal && dpSakVerdi is BigDecimal) {
            arenaVerdi.compareTo(dpSakVerdi) == 0
        } else {
            arenaVerdi == dpSakVerdi
        }
    }

    fun kontrollerResultat(
        verdi: OpplysningsverdiDTO,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningKontrollResultat =
        kontroller(
            verdi =
                verdiFraResultat(verdi) ?: throw IllegalArgumentException(
                    "Opplysning $opplysningsId forventer verdi av type ${verdiType.simpleName}",
                ),
            migreringId = migreringId,
            repository = repository,
        )

    fun verdiFraResultat(verdi: OpplysningsverdiDTO): T?

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
