package no.nav.dagpenger.migrering.arena.migrering.opplysninger

import io.github.oshai.kotlinlogging.KotlinLogging
import io.prometheus.metrics.core.metrics.Counter
import no.nav.dagpenger.migrering.arena.migrering.MigreringRepository
import no.nav.dagpenger.migrering.arena.migrering.MigreringsKontekst
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
        opplysning: Opplysning<Any>,
        migreringId: UUID,
        repository: MigreringRepository,
    ): OpplysningVerifisertResultat {
        val arenaOpplysning =
            repository.hentOpplysning(
                migreringId = migreringId,
                opplysningId = opplysning.uuid,
            )

        return OpplysningVerifisertResultat(
            uuid = opplysningsId,
            navn = opplysningsNavn,
            verifisert = arenaOpplysning?.verdi == opplysning.verdi && arenaOpplysning?.uuid == opplysning.uuid,
            dpSakVerdi = opplysning.verdi,
            arenaVerdi = arenaOpplysning?.verdi,
        )
    }

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
